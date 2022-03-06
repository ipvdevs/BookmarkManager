package bg.sofia.uni.fmi.mjt.bookmarks.manager.entity;

import com.google.gson.annotations.SerializedName;

public class ShortenUrl {
    @SerializedName("created_at")
    private String createdAt;
    private String id;
    private String link;

    public String createdAt() {
        return createdAt;
    }

    public String id() {
        return id;
    }

    public String link() {
        return link;
    }
}
