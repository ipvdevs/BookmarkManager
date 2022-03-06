package bg.sofia.uni.fmi.mjt.bookmarks.manager.external;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class HtmlParser {

    public Document parse(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

}
