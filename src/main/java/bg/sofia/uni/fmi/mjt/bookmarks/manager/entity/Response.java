package bg.sofia.uni.fmi.mjt.bookmarks.manager.entity;

public record Response<T>(Status status, T response) {
}
