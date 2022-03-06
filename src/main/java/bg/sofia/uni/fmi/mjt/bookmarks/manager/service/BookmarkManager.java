package bg.sofia.uni.fmi.mjt.bookmarks.manager.service;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Status;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.StopWordsException;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.UrlShortenerException;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.external.HtmlParser;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.external.UrlShortenerService;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.logger.Level;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.repository.BookmarkStorage;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.repository.Bookmarks;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.Pair;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.StopWords;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.chrome.BookmarkCategories;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.chrome.ChromeBookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.chrome.ChromeImport;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.cli.CliPrompts;
import com.google.gson.Gson;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.StopWords.STOPWORDS_BG_PATH;
import static bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.StopWords.STOPWORDS_EN_PATH;

public class BookmarkManager implements BookmarkService {
    private static final String WHITESPACE_PUNCTUATION_REGEX = "[\\p{IsPunctuation}\\p{IsWhite_Space}]+";

    private static final String INTERNAL_ERROR_MESSAGE = "An internal problem occurred. " +
                                                         "Please try again or contact an administrator";
    private static final int MAX_KEYWORDS = 20;
    private static final int MIN_KEYWORD_LENGTH = 3;

    private final UrlShortenerService shortener;
    private final BookmarkStorage storage;
    private final HtmlParser htmlParser;
    private final HttpClient httpClient;

    public BookmarkManager(BookmarkStorage storage,
                           UrlShortenerService shortener,
                           HtmlParser htmlParser,
                           HttpClient httpClient) {
        this.storage = storage;
        this.shortener = shortener;
        this.htmlParser = htmlParser;
        this.httpClient = httpClient;
    }

    @Override
    public Response<String> createGroup(String groupName, String caller) {
        if (checkNulls(groupName, caller)) {
            String logMsg = BookmarkManager.class + " createGroup(...): null argument.";
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);

            return new Response<>(Status.ERROR, INTERNAL_ERROR_MESSAGE);
        }

        Bookmarks target = storage.get(caller).orElse(storage.hook(caller));

        if (target.contains(groupName)) {
            return new Response<>(
                    Status.ERROR,
                    String.format("Group with name %s already exists.", groupName)
            );
        }

        target.newGroup(groupName);

        return new Response<>(
                Status.OK,
                String.format("Group with name %s is created.", groupName)
        );
    }

    @Override
    public Response<String> addTo(String groupName, String url, String caller) {
        Pair<Boolean, String> valid = addToValidation(groupName, url, caller);

        if (!valid.first()) {
            return new Response<>(Status.ERROR, valid.second());
        }

        Bookmark bookmark;
        try {
            bookmark = generateBookmark(url);
        } catch (IllegalArgumentException | IOException e) {
            String logMsg = BookmarkManager.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.INFO, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.INFO, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            return new Response<>(Status.ERROR, "The provided bookmark's url is invalid.");
        }

        Bookmarks target = storage.get(caller).orElse(storage.hook(caller));

        if (target.containsBookmark(groupName, bookmark)) {
            return new Response<>(
                    Status.ERROR,
                    String.format("%s already exists in %s.", bookmark.url(), groupName)
            );
        }

        target.addBookmark(groupName, bookmark);

        return new Response<>(
                Status.OK,
                String.format("%s added to %s.", bookmark.url(), groupName)
        );
    }

    @Override
    public Response<String> addToShorten(String groupName, String url, String caller) {
        Pair<Boolean, String> valid = addToValidation(groupName, url, caller);

        if (!valid.first()) {
            return new Response<>(Status.ERROR, valid.second());
        }

        String shortenUrl;
        try {
            shortenUrl = shortener.shorten(url);
        } catch (UrlShortenerException e) {
            String logMsg = BookmarkManager.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            return new Response<>(
                    Status.ERROR,
                    "A problem occurred with the URL shortener service."
            );
        }

        return addTo(groupName, shortenUrl, caller);
    }

    @Override
    public Response<String> removeFrom(String groupName, String url, String caller) {
        if (checkNulls(groupName, url, caller)) {
            String logMsg = BookmarkManager.class + " removeFrom(...): null argument.";
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);

            return new Response<>(Status.ERROR, INTERNAL_ERROR_MESSAGE);
        }

        Bookmarks target = storage.get(caller).orElse(storage.hook(caller));

        if (!target.contains(groupName)) {
            return new Response<>(
                    Status.ERROR,
                    String.format("A bookmark group with name %s does not exist.", groupName)
            );
        }

        try {
            target.removeBookmark(groupName, url);
        } catch (IllegalStateException e) {
            String logMsg = BookmarkManager.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            return new Response<>(
                    Status.ERROR,
                    String.format("A bookmark with url %s does not exist.", url)
            );
        }

        return new Response<>(
                Status.OK,
                String.format("%s removed from %s.", url, groupName)
        );
    }

    @Override
    public Response<String> searchByTitle(String title, String caller) {
        if (checkNulls(title, caller)) {
            String logMsg = BookmarkManager.class + " searchByTitle(...): null argument.";
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);

            return new Response<>(Status.ERROR, INTERNAL_ERROR_MESSAGE);
        }

        if (title.isEmpty()) {
            return new Response<>(Status.ERROR, "Title is empty.");
        }

        Bookmarks target = storage.get(caller).orElse(storage.hook(caller));

        List<Bookmark> found = target.collectByTitle(title);

        String result = CliPrompts.header() +
                        listPretty(title, found);

        return new Response<>(Status.OK, result);
    }


    @Override
    public Response<String> searchByTags(List<String> tags, String caller) {
        if (checkNulls(tags, caller)) {
            String logMsg = BookmarkManager.class + " searchByTags(...): null argument.";
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);

            return new Response<>(Status.ERROR, INTERNAL_ERROR_MESSAGE);
        }

        Bookmarks target = storage.get(caller).orElse(storage.hook(caller));

        List<Bookmark> found = target.collectByTags(tags);

        String result = CliPrompts.header() +
                        listPretty(tags.toString(), found);

        return new Response<>(Status.OK, result);
    }

    @Override
    public Response<String> list(String caller) {
        Bookmarks target = storage.get(caller).orElse(storage.hook(caller));

        String listAll = listAllPretty(target);

        return new Response<>(Status.OK, listAll);
    }

    @Override
    public Response<String> list(String groupName, String caller) {
        Bookmarks target = storage.get(caller).orElse(storage.hook(caller));

        if (!target.contains(groupName)) {
            return new Response<>(
                    Status.ERROR,
                    String.format("A group with name %s does not exist.", groupName)
            );
        }

        return new Response<>(Status.OK, listPretty(groupName, target.getGroup(groupName)));
    }

    @Override
    public Response<String> cleanup(String caller) {
        Bookmarks target = storage.get(caller).orElse(storage.hook(caller));

        Collection<String> urls = target.collectUrls();

        int removed = 0;
        for (String url : urls) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

            HttpResponse<String> response;
            try {
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                String logMsg = BookmarkManager.class + " " + e.getMessage();
                Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), logMsg);
                Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

                return new Response<>(Status.ERROR, "Could not remove a bookmark with url " + url);
            }

            if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                synchronized (this) {
                    target.removeBookmark(url);
                    ++removed;
                }
            }
        }

        return new Response<>(Status.OK, "Cleanup completed. Totally removed invalid bookmarks: " + removed);
    }

    @Override
    public Response<String> importFromChrome(String caller) {
        Bookmarks target = storage.get(caller).orElse(storage.hook(caller));

        String osName = System.getProperty("os.name");
        String osUser = System.getProperty("user.name");

        String pathToBookmarks;
        if (osName.toLowerCase().contains("linux")) {
            pathToBookmarks = String.format("/home/%s/.config/google-chrome/Default/Bookmarks", osUser);
        } else if (osName.toLowerCase().contains("windows")) {
            pathToBookmarks = "AppData\\Local\\Google\\Chrome\\User Data\\Default\\Bookmarks";
        } else if (osName.toLowerCase().contains("mac")) {
            pathToBookmarks = String.format("/Users/%s/Library/Application\\ Support/Google/Chrome", osUser);
        } else {
            return new Response<>(Status.ERROR, osName + " not supported.");
        }

        if (!target.contains("Chrome")) {
            createGroup("Chrome", caller);
        }

        try (var br = Files.newBufferedReader(Path.of(pathToBookmarks))) {
            Gson gson = new Gson();
            ChromeImport chromeImport = gson.fromJson(br, ChromeImport.class);

            BookmarkCategories root = chromeImport.getRoot();

            root.getBookmarkBar().getChildren().stream()
                    .map(ChromeBookmark::getUrl)
                    .forEach(url -> addTo("Chrome", url, caller));

        } catch (IOException e) {
            String logMsg = BookmarkManager.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));
        }

        return new Response<>(Status.OK, "Chrome import completed. Bookmarks imported to group Chrome.");
    }

    private String listAllPretty(Bookmarks bookmarks) {
        StringBuilder builder = new StringBuilder(CliPrompts.header());

        Collection<String> groups = bookmarks.getNames();

        for (String groupName : groups) {
            builder.append(listPretty(groupName, bookmarks.getGroup(groupName)));
        }

        return builder.toString();
    }

    private String listPretty(String title, Collection<Bookmark> bookmarks) {
        StringBuilder builder = new StringBuilder();

        builder.append(title).append(": ").append(System.lineSeparator());

        for (Bookmark bookmark : bookmarks) {
            builder.append(System.lineSeparator());
            builder.append(bookmark);
            builder.append(System.lineSeparator());
        }

        builder.append(CliPrompts.header());

        return builder.toString();
    }

    private Pair<Boolean, String> addToValidation(String groupName, String url, String caller) {
        if (checkNulls(groupName, url, caller)) {
            String logMsg = BookmarkManager.class + " addToValidation(...): null argument.";
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);

            return new Pair<>(false, INTERNAL_ERROR_MESSAGE);
        }

        Bookmarks target = storage.get(caller).orElse(storage.hook(caller));

        if (!target.contains(groupName)) {
            return new Pair<>(
                    false,
                    String.format("A group with name %s does not exist.", groupName)
            );
        }

        return new Pair<>(true, "VALID");
    }

    public Bookmark generateBookmark(String url) throws IOException {
        Document doc = htmlParser.parse(url);

        StopWords stopWords = new StopWords();

        try {
            stopWords.load(STOPWORDS_EN_PATH);
            stopWords.load(STOPWORDS_BG_PATH);
        } catch (StopWordsException e) {
            String logMsg = BookmarkManager.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));
        }

        Map<String, Integer> wordOccurrences = new HashMap<>();

        Arrays.stream(doc.body().text().split(WHITESPACE_PUNCTUATION_REGEX))
                .map(String::strip)
                .map(String::toLowerCase)
                .filter(word -> word.length() > MIN_KEYWORD_LENGTH)
                .filter(Predicate.not(stopWords::isStopWord))
                .forEach(word -> {
                    wordOccurrences.merge(word, 1, Integer::sum);
                });

        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(wordOccurrences.entrySet());

        List<String> tags = entryList.stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(MAX_KEYWORDS)
                .toList();

        return new Bookmark(doc.title(), new URL(url), tags);
    }

    @SafeVarargs
    private <T> boolean checkNulls(T... args) {
        for (T str : args) {
            if (str == null) {
                return true;
            }
        }

        return false;
    }

}
