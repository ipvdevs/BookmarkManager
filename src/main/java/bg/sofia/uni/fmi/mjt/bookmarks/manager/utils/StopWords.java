package bg.sofia.uni.fmi.mjt.bookmarks.manager.utils;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.StopWordsException;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.logger.Level;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StopWords {
    private static final String FAILED_LOADING = "Failed to load stopwords!";

    public static final String STOPWORDS_EN_PATH = "src/main/resources/stopwords_en.txt";
    public static final String STOPWORDS_BG_PATH = "src/main/resources/stopwords_bg.txt";

    private final Set<String> stopWords = new HashSet<>();

    public void load(String path) throws StopWordsException {
        if (path == null) {
            String logMsg = StopWords.class + " load(String path): reader is null.";
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), logMsg);

            throw new StopWordsException(FAILED_LOADING);
        }

        try (var fr = new FileReader(path)) {
            load(fr);
        } catch (IOException e) {
            String logMsg = StopWords.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), logMsg);

            throw new StopWordsException(FAILED_LOADING, e);
        }
    }

    public void load(Reader reader) throws StopWordsException {
        if (reader == null) {
            String logMsg = StopWords.class + " load(Reader reader): reader is null.";
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), logMsg);

            throw new StopWordsException(FAILED_LOADING);
        }

        try (var br = new BufferedReader(reader)) {
            this.stopWords.addAll(br.lines().collect(Collectors.toSet()));
        } catch (IOException e) {
            String logMsg = StopWords.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), logMsg);

            throw new StopWordsException(FAILED_LOADING, e);
        }
    }

    public boolean isStopWord(String word) {
        return stopWords.contains(word);
    }

}
