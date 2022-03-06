package bg.sofia.uni.fmi.mjt.bookmarks.manager.repository;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.BookmarkStorageException;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.logger.Level;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BookmarkStorage implements FileRepository<String, Bookmarks> {
    private Map<String, Bookmarks> bookmarkStorage;

    public BookmarkStorage() {
        this.bookmarkStorage = new HashMap<>();
    }

    @Override
    public void store(String file) {
        try (var fw = new FileWriter(file)) {
            store(fw);
        } catch (Exception e) {
            String logMsg = BookmarkStorage.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            throw new BookmarkStorageException("Could not store bookmarks data.");
        }
    }

    @Override
    public void store(Writer writer) {
        try (var bw = new BufferedWriter(writer)) {
            Gson gson = new Gson();
            bw.write(gson.toJson(bookmarkStorage, Map.class));
        } catch (Exception e) {
            String logMsg = BookmarkStorage.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            throw new BookmarkStorageException("Could not store bookmarks data.");
        }
    }

    @Override
    public void load(String file) {
        try (var fr = new FileReader(file)) {
            load(fr);
        } catch (Exception e) {
            String logMsg = BookmarkStorage.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            throw new BookmarkStorageException("Could not load bookmarks data.");
        }
    }

    @Override
    public void load(Reader reader) {
        try (var bw = new BufferedReader(reader)) {
            Gson gson = new Gson();
            this.bookmarkStorage = gson.fromJson(bw, new TypeToken<Map<String, Bookmarks>>() {
            }.getType());
        } catch (Exception e) {
            String logMsg = BookmarkStorage.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            throw new BookmarkStorageException("Could not load bookmarks data.");
        }
    }

    @Override
    public void add(String key, Bookmarks value) {
        bookmarkStorage.putIfAbsent(key, value);
    }

    @Override
    public void remove(String key) {
        if (!bookmarkStorage.containsKey(key)) {
            return;
        }

        bookmarkStorage.remove(key);
    }

    @Override
    public Optional<Bookmarks> get(String key) {
        if (bookmarkStorage.containsKey(key)) {
            return Optional.of(bookmarkStorage.get(key));
        }

        return Optional.empty();
    }

    @Override
    public boolean contains(String key) {
        return bookmarkStorage.containsKey(key);
    }

    @Override
    public Collection<Bookmarks> getAll() {
        return bookmarkStorage.values();
    }

    public Bookmarks hook(String caller) {
        add(caller, new Bookmarks());

        return bookmarkStorage.get(caller);
    }
}
