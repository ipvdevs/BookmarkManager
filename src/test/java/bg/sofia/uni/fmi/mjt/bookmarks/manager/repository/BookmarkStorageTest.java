package bg.sofia.uni.fmi.mjt.bookmarks.manager.repository;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.BookmarkStorageException;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookmarkStorageTest {

    Bookmarks bookmarks = new Bookmarks();
    BookmarkStorage storage = new BookmarkStorage();

    @Test
    void store() {
        String message = "The writer does not match the expected json format. Check the GSON parsing and formatting.";

        StringWriter writer = new StringWriter();

        storage.add("username", bookmarks);
        storage.store(writer);

        assertEquals("{\"username\":{\"groups\":{},\"urlMap\":{}}}", writer.toString(), message);
    }

    @Test
    void load() {
        String message = "The writer content should match the reader one. Check the GSON parsing and formatting.";

        StringReader reader = new StringReader("{\"username\":{\"groups\":{},\"urlMap\":{}}}");
        StringWriter writer = new StringWriter();

        storage.load(reader);
        storage.store(writer);

        assertEquals("{\"username\":{\"groups\":{},\"urlMap\":{}}}", writer.toString(), message);
    }

    @Test
    void storeWithNullWriter() {
        String message = "The provided writer is null. Exception should be thrown.";

        assertThrows(BookmarkStorageException.class, () -> storage.store((Writer) null), message);
    }

    @Test
    void storeWithNullPathString() {
        String message = "The provided path String is null. Exception should be thrown.";

        assertThrows(BookmarkStorageException.class, () -> storage.store((String) null), message);
    }

    @Test
    void loadWithNullReader() {
        String message = "The provided reader is null. Exception should be thrown.";

        assertThrows(BookmarkStorageException.class, () -> storage.load((Reader) null), message);
    }

    @Test
    void loadWithNullPathString() {
        String message = "The provided provided path String  is null. Exception should be thrown.";

        assertThrows(BookmarkStorageException.class, () -> storage.load((String) null), message);
    }

    @Test
    void add() {
        String message = "New bookmarks storage should be created for user \"username\"";

        storage.add("username", bookmarks);

        assertTrue(storage.contains("username"), message);
    }

    @Test
    void remove() {
        storage.add("username", bookmarks);
        assertTrue(storage.contains("username"), "Bookmarks storage for user \"username\" should be created.");
        storage.remove("username");
        assertFalse(storage.contains("username"), "Bookmarks storage for user \"username\" should be removed.");
    }

    @Test
    void getAll() {
        storage.add("username1", bookmarks);
        storage.add("username2", bookmarks);

        assertEquals(2, storage.getAll().size(), "Bookmarks storage is created for two users.");
    }

    @Test
    void hook() {
        assertTrue(storage.getAll().isEmpty(), "There are no storages created.");
        storage.hook("username");
        assertFalse(storage.getAll().isEmpty(), "There is one storage created (hooked).");
        assertTrue(storage.contains("username"), "The owner of this storage is user \"username\".");
    }
}