package bg.sofia.uni.fmi.mjt.bookmarks.manager.utils;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.StopWordsException;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StopWordsTest {

    @Test
    void loadFromReader() throws StopWordsException {
        StringReader reader = new StringReader("or");

        StopWords stopWords = new StopWords();
        stopWords.load(reader);

        assertTrue(stopWords.isStopWord("or"), "The word \"or\" is added as stopword.");
    }

    @Test
    void loadFromNullReader() {
        StopWords stopWords = new StopWords();

        assertThrows(StopWordsException.class, () -> stopWords.load((Reader) null), "The reader should not be null.");
    }

    @Test
    void loadFromNullPath() {
        StopWords stopWords = new StopWords();

        assertThrows(StopWordsException.class, () -> stopWords.load((String) null), "The path should not be null.");
    }

    @Test
    void loadFromInvalidPath() {
        StopWords stopWords = new StopWords();

        assertThrows(StopWordsException.class, () -> stopWords.load("invalid"), "The provided path is not valid. Exception should be thrown");
    }
}