package bg.sofia.uni.fmi.mjt.bookmarks.manager.service;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Response;

import java.util.List;

public interface BookmarkService {

    Response<String> addTo(String groupName, String url, String caller);

    Response<String> addToShorten(String groupName, String url, String caller);

    Response<String> removeFrom(String groupName, String url, String caller);

    Response<String> searchByTitle(String title, String caller);

    Response<String> searchByTags(List<String> tags, String caller);

    Response<String> createGroup(String groupName, String caller);

    Response<String> list(String caller);

    Response<String> list(String groupName, String caller);

    Response<String> cleanup(String caller);

    Response<String> importFromChrome(String caller);
}
