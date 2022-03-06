package bg.sofia.uni.fmi.mjt.bookmarks.manager.utils;

public class Pair<V, U> {
    private final V first;
    private final U second;

    public Pair(V first, U second) {
        this.first = first;
        this.second = second;
    }

    public V first() {
        return first;
    }

    public U second() {
        return second;
    }
};
