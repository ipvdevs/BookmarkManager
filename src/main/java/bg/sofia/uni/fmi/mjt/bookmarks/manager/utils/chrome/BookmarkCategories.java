package bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.chrome;

import com.google.gson.annotations.SerializedName;

public class BookmarkCategories {
    @SerializedName("bookmark_bar")
    private BookmarksFolder bookmarkBar;
    private BookmarksFolder other;
    private BookmarksFolder synced;

    public BookmarksFolder getBookmarkBar() {
        return bookmarkBar;
    }

    public BookmarksFolder getOther() {
        return other;
    }

    public BookmarksFolder getSynced() {
        return synced;
    }
}
