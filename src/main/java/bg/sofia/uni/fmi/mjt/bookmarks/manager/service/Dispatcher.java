package bg.sofia.uni.fmi.mjt.bookmarks.manager.service;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.Server;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.external.BiltyShortener;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.external.HtmlParser;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.external.UrlShortenerService;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.logger.DefaultLogger;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.logger.Logger;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.logger.LoggerOptions;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.repository.BookmarkStorage;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.repository.UserStorage;

import java.net.http.HttpClient;

public class Dispatcher {
    private static final String LOGS_DIR = "logs";
    private static final Logger LOGGER = new DefaultLogger(new LoggerOptions(Server.class, LOGS_DIR));

    private static final BookmarkStorage BOOKMARK_STORAGE = new BookmarkStorage();
    private static final UrlShortenerService SHORTENER_SERVICE = new BiltyShortener();
    private static final HtmlParser HTML_PARSER = new HtmlParser();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final BookmarkManager BOOKMARK_MANAGER = new BookmarkManager(
            BOOKMARK_STORAGE,
            SHORTENER_SERVICE,
            HTML_PARSER,
            HTTP_CLIENT
    );

    private static final UserStorage USER_STORAGE = new UserStorage();
    private static final AuthService AUTH_MANAGER = new AuthManager(USER_STORAGE);

    public static AuthService authManager() {
        return AUTH_MANAGER;
    }

    public static BookmarkManager bookmarkManager() {
        return BOOKMARK_MANAGER;
    }

    public static Logger logger() {
        return LOGGER;
    }

    public static BookmarkStorage bookmarkStorage() {
        return BOOKMARK_STORAGE;
    }

    public static UserStorage userStorage() {
        return USER_STORAGE;
    }
}
