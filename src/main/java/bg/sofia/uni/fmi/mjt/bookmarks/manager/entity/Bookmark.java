package bg.sofia.uni.fmi.mjt.bookmarks.manager.entity;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Bookmark {
    private final String title;
    private final URL url;
    private final List<String> tags;

    private static final int MAX_TAGS_PRINT = 5;

    public Bookmark(String title, URL url, List<String> tags) {
        this.title = title;
        this.url = url;
        this.tags = tags;
    }

    @Override
    public String toString() {
        String tagsPretty = tags.stream()
                .limit(MAX_TAGS_PRINT)
                .collect(Collectors.joining(", "));

        return String.format(
                "TITLE: %s%n" +
                "LINK: %s%n" +
                "TAGS: [%s, ...]", title, url, tagsPretty);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bookmark bookmark = (Bookmark) o;
        return Objects.equals(title, bookmark.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    public String title() {
        return title;
    }

    public URL url() {
        return url;
    }

    public List<String> tags() {
        return tags;
    }
}
