package bg.sofia.uni.fmi.mjt.bookmarks.manager.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PairTest {

    @Test
    void createPair() {
        Pair<String, Integer> pair = new Pair<>("test", 123);

        assertEquals("test", pair.first(), "The first element should be set to \"test\"");
        assertEquals(123, pair.second(), "The first element should be set to 123");
    }

}