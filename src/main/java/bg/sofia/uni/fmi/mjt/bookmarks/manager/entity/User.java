package bg.sofia.uni.fmi.mjt.bookmarks.manager.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A model representing application user.
 */
public class User {
    private final String username;
    private final String pwHash;
    private final String createdOn;

    public User(String username, String pwHash) {
        this.username = username;
        this.pwHash = pwHash;
        this.createdOn = LocalDateTime.now().toString();
    }


    public String getUsername() {
        return username;
    }

    public String getPwHash() {
        return pwHash;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
