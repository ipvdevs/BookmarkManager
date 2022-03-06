package bg.sofia.uni.fmi.mjt.bookmarks.manager.repository;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.User;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.UserStorageException;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserStorageTest {
    UserStorage storage = new UserStorage();

    @Test
    void store() {
        String message = "The writer does not match the expected json format. Check the GSON parsing and formatting.";

        StringWriter writer = new StringWriter();

        User user = new User("username", "hashedPw");

        String expected =
                String.format("{\"username\":{\"%s\":\"username\",\"pwHash\":\"%s\",\"createdOn\":\"%s\"}}",
                        user.getUsername(), user.getPwHash(), user.getCreatedOn());

        storage.add("username", user);
        storage.store(writer);

        assertEquals(expected, writer.toString(), message);
    }

    @Test
    void load() {
        String message = "The writer content should match the reader one. Check the GSON parsing and formatting.";

        String content = String.format("{\"username\":{\"%s\":\"username\",\"pwHash\":\"%s\",\"createdOn\":\"%s\"}}",
                "username", "pwHashed", "01/01/2022");
        StringReader reader = new StringReader(content);

        StringWriter writer = new StringWriter();

        storage.load(reader);
        storage.store(writer);

        assertEquals(content, writer.toString(), message);
    }

    @Test
    void storeWithNullWriter() {
        String message = "The provided writer is null. Exception should be thrown.";

        assertThrows(UserStorageException.class, () -> storage.store((Writer) null), message);
    }

    @Test
    void storeWithNullPathString() {
        String message = "The provided path String is null. Exception should be thrown.";

        assertThrows(UserStorageException.class, () -> storage.store((String) null), message);
    }

    @Test
    void loadWithNullReader() {
        String message = "The provided reader is null. Exception should be thrown.";

        assertThrows(UserStorageException.class, () -> storage.load((Reader) null), message);
    }

    @Test
    void loadWithNullPathString() {
        String message = "The provided provided path String is null. Exception should be thrown.";

        assertThrows(UserStorageException.class, () -> storage.load((String) null), message);
    }

    @Test
    void remove() {
        storage.add("username", new User("uname", "pw"));
        assertTrue(storage.contains("username"), "User with username \"uname\" should be added.");
        storage.remove("username");
        assertFalse(storage.contains("username"), "User with username \"uname\" should be removed.");
    }

}