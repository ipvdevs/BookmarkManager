package bg.sofia.uni.fmi.mjt.bookmarks.manager;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.logger.Level;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;
import com.google.gson.Gson;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

public class ServerConfig {
    private static final String DEFAULT_HOSTNAME = "localhost";
    private static final int DEFAULT_PORT = 62535;
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private final String hostname;
    private final int port;
    private final int bufferSize;
    private final boolean isBufferDirect;
    private final boolean isBlocking;

    /**
     * Constructs a server configuration with default settings.
     */
    private ServerConfig() {
        this.hostname = DEFAULT_HOSTNAME;
        this.port = DEFAULT_PORT;
        this.bufferSize = DEFAULT_BUFFER_SIZE;
        this.isBufferDirect = false;
        this.isBlocking = false;
    }

    /**
     * Creates a server configuration with default settings.
     *
     * @return ServerConfig - Default configuration with preset settings.
     */
    public static ServerConfig newDefaultServerConfig() {
        return new ServerConfig();
    }

    /**
     * Loads a server configuration (JSON format) from reader.
     * If a runtime problem occurs with the reader or the file format,
     * the default configuration is loaded.
     *
     * @param reader The reader from which data is extracted and parsed.
     * @return Returns the ServerConfig with specified or default settings.
     */
    public static ServerConfig newCustomServerConfig(Reader reader) {
        Gson gson = new Gson();

        try {
            Objects.requireNonNull(reader, "Reader is null.");
            return gson.fromJson(reader, ServerConfig.class);
        } catch (Exception e) {
            String logMsg = ServerConfig.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            return newDefaultServerConfig();
        }
    }

    /**
     * Loads a server configuration (JSON format) from file with given path.
     * If a runtime problem occurs with the path or the file and its format,
     * the default configuration is loaded.
     *
     * @param configFile The configuration file in JSON format from which the settings
     *                   are extracted and parsed.
     * @return Returns the ServerConfig with specified or default settings.
     */
    public static ServerConfig newCustomServerConfig(Path configFile) {
        try {
            Objects.requireNonNull(configFile, "ConfigFile is null.");
            var br = Files.newBufferedReader(configFile);
            return newCustomServerConfig(br);
        } catch (Exception e) {
            String logMsg = ServerConfig.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.WARN, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            return newDefaultServerConfig();
        }
    }

    public String hostname() {
        return hostname;
    }

    public int port() {
        return port;
    }

    public int bufferSize() {
        return bufferSize;
    }

    public boolean isBufferDirect() {
        return isBufferDirect;
    }

    public boolean isBlocking() {
        return isBlocking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerConfig that = (ServerConfig) o;
        return port == that.port &&
               bufferSize == that.bufferSize &&
               isBufferDirect() == that.isBufferDirect() &&
               isBlocking() == that.isBlocking() &&
               Objects.equals(hostname, that.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname, port, bufferSize, isBufferDirect(), isBlocking());
    }
}
