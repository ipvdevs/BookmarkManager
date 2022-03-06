package bg.sofia.uni.fmi.mjt.bookmarks.manager.external;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.UrlShortenerException;

public interface UrlShortenerService {

    String shorten(String url) throws UrlShortenerException;

}
