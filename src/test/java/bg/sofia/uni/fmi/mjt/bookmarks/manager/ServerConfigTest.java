package bg.sofia.uni.fmi.mjt.bookmarks.manager;

import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerConfigTest {

    @Test
    void loadFromReaderWithInvalidJsonFormat() {
        String message = "The JSON format was not valid. A default configuration should be loaded.";

        ServerConfig expected = ServerConfig.newDefaultServerConfig();
        StringReader reader = new StringReader("{invalid}");
        ServerConfig actual = ServerConfig.newCustomServerConfig(reader);

        assertEquals(expected, actual, message);
    }

    @Test
    void loadFromReaderWithNullReader() {
        String message = "The provided reader has a value of null. " +
                         "A default configuration should be loaded.";

        ServerConfig expected = ServerConfig.newDefaultServerConfig();
        ServerConfig actual = ServerConfig.newCustomServerConfig((Reader) null);

        assertEquals(expected, actual, message);
    }

    @Test
    void loadFromReaderWithValidJsonFormat() {
        String message = "The loaded settings do not match the settings from the config file.";

        String json = """
                      {
                        "hostname": "test_hostname",
                        "port": 1234,
                        "bufferSize": 2048,
                        "isBufferDirect": true,
                        "isBlocking": true
                      }
                      """;
        StringReader reader = new StringReader(json);

        ServerConfig actual = ServerConfig.newCustomServerConfig(reader);

        assertEquals("test_hostname", actual.hostname(), message);
        assertEquals(1234, actual.port(), message);
        assertEquals(2048, actual.bufferSize(), message);
        assertTrue(actual.isBlocking(), message);
        assertTrue(actual.isBufferDirect(), message);
    }

    @Test
    void loadFromFileWithInvalidPath() {
        String message = "The provided file path is not valid." +
                         "A default configuration should be loaded.";

        ServerConfig actual = ServerConfig.newCustomServerConfig(Path.of(""));
        ServerConfig expected = ServerConfig.newDefaultServerConfig();

        assertEquals(expected, actual, message);
    }

    @Test
    void loadFromFileWithNullPath() {
        String message = "The provided file path is null." +
                         "A default configuration should be loaded.";

        ServerConfig actual = ServerConfig.newCustomServerConfig((Path) null);
        ServerConfig expected = ServerConfig.newDefaultServerConfig();

        assertEquals(expected, actual, message);
    }
}