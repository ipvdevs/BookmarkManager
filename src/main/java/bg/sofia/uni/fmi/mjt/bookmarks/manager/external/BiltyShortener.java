package bg.sofia.uni.fmi.mjt.bookmarks.manager.external;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.ShortenUrl;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.UrlShortenerException;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.logger.Level;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * Utility class to provide usage of URL Shorten API.
 */
final public class BiltyShortener implements UrlShortenerService {
    private static final String BITLY_API_URL = "https://api-ssl.bitly.com/v4/shorten";
    private static final String AUTH_HEADER_VALUE = "INSERT_API_KEY_HERE";

    private final Gson gsonParser;

    public BiltyShortener() {
        this.gsonParser = new Gson();
    }

    @Override
    public String shorten(String url) throws UrlShortenerException {
        Objects.requireNonNull(url, "Url is null!");

        String body = String.format("{\"long_url\":\"%s\"}", url);

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BITLY_API_URL))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", AUTH_HEADER_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            String logMsg = BiltyShortener.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            throw new UrlShortenerException("A problem occurred with the url shortening.", e);
        }

        if (response.statusCode() != HttpURLConnection.HTTP_CREATED &&
            response.statusCode() != HttpURLConnection.HTTP_OK) {
            String logMsg = BiltyShortener.class + " Url shortener API invalid status code: " + response.statusCode();
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), logMsg);

            throw new UrlShortenerException();
        }

        ShortenUrl shorten = gsonParser.fromJson(response.body(), ShortenUrl.class);

        return shorten.link();
    }
}
