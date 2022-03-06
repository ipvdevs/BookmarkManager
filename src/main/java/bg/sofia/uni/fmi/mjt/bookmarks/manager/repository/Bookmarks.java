package bg.sofia.uni.fmi.mjt.bookmarks.manager.repository;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Bookmark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Bookmarks {
    private final Map<String, Set<Bookmark>> groups = new HashMap<>();
    private final Map<String, Bookmark> urlMap = new HashMap<>();

    public void newGroup(String groupName) {
        groups.putIfAbsent(groupName, new HashSet<>());
    }

    public boolean contains(String groupName) {
        return groups.containsKey(groupName);
    }

    public Collection<Bookmark> getGroup(String groupName) {
        return groups.get(groupName);
    }

    public Collection<String> getNames() {
        return groups.keySet();
    }

    public void addBookmark(String groupName, Bookmark bookmark) {
        groups.get(groupName).add(bookmark);
        urlMap.put(bookmark.url().toString(), bookmark);
    }

    public boolean containsBookmark(String groupName, Bookmark bookmark) {
        return groups.get(groupName).contains(bookmark);
    }

    public void removeBookmark(String groupName, String url) {
        if (!urlMap.containsKey(url) || !groups.containsKey(groupName)) {
            throw new IllegalStateException();
        }

        Bookmark toRemove = urlMap.get(url);
        groups.get(groupName).remove(toRemove);
        urlMap.remove(url);
    }

    public void removeBookmark(String url) {
        if (!urlMap.containsKey(url)) {
            throw new IllegalStateException();
        }

        Bookmark toRemove = urlMap.get(url);

        for (Set<Bookmark> bookmarks : groups.values()) {
            bookmarks.remove(toRemove);
        }

        urlMap.remove(url);
    }

    public Set<String> collectUrls() {
        return urlMap.keySet();
    }

    public List<Bookmark> collectByTitle(String title) {
        return urlMap.values().stream()
                .filter(bookmark -> bookmark.title().contains(title))
                .toList();
    }

    public List<Bookmark> collectByTags(List<String> tags) {
        List<Bookmark> result = new ArrayList<>();

        for (var entry : urlMap.entrySet()) {
            Bookmark bookmark = entry.getValue();

            boolean matchesTag = bookmark.tags().stream().anyMatch(tags::contains);

            if (matchesTag) {
                result.add(bookmark);
            }
        }

        return result;
    }
}
