package bg.sofia.uni.fmi.mjt.bookmarks.manager.repository;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.User;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.UserStorageException;
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

public class UserStorage implements FileRepository<String, User> {
    private Map<String, User> userStorage;

    public UserStorage() {
        this.userStorage = new HashMap<>();
    }

    @Override
    public void add(String username, User user) {
        userStorage.putIfAbsent(username, user);
    }

    @Override
    public void remove(String username) {
        userStorage.remove(username);
    }

    @Override
    public Optional<User> get(String username) {
        if (!userStorage.containsKey(username)) {
            return Optional.empty();
        }

        return Optional.of(userStorage.get(username));
    }

    @Override
    public boolean contains(String username) {
        return userStorage.containsKey(username);
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.values();
    }

    @Override
    public void store(String file) {
        try (var fw = new FileWriter(file)) {
            store(fw);
        } catch (Exception e) {
            String logMsg = UserStorage.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            throw new UserStorageException("Could not store users data.", e);
        }
    }

    @Override
    public void store(Writer writer) {
        try (var bw = new BufferedWriter(writer)) {
            Gson gson = new Gson();
            bw.write(gson.toJson(userStorage, Map.class));
        } catch (Exception e) {
            String logMsg = UserStorage.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            throw new UserStorageException("Could not store users data.", e);
        }
    }

    @Override
    public void load(String file) {
        try (var fr = new FileReader(file)) {
            load(fr);
        } catch (Exception e) {
            String logMsg = UserStorage.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            throw new UserStorageException("Could not load users data.", e);
        }
    }

    @Override
    public void load(Reader reader) {
        try (var bw = new BufferedReader(reader)) {
            Gson gson = new Gson();
            this.userStorage = gson.fromJson(bw, new TypeToken<Map<String, User>>() {
            }.getType());
        } catch (Exception e) {
            String logMsg = UserStorage.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            throw new UserStorageException("Could not load users data.", e);
        }
    }
}
