package bg.sofia.uni.fmi.mjt.bookmarks.manager.logger;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.LogException;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultLoggerTest {
    private final String tmpDir = System.getProperty("java.io.tmpdir");

    @Test
    void logWithNullLevel() {
        LoggerOptions loggerOptions = new LoggerOptions(DefaultLoggerTest.class, tmpDir);
        Logger logger = new DefaultLogger(loggerOptions);

        assertThrows(IllegalArgumentException.class, () -> logger.log(null, LocalDateTime.now(), "msg"));
    }

    @Test
    void logWithNullTimestamp() {
        LoggerOptions loggerOptions = new LoggerOptions(DefaultLoggerTest.class, tmpDir);
        Logger logger = new DefaultLogger(loggerOptions);

        assertThrows(IllegalArgumentException.class, () -> logger.log(Level.DEBUG, null, "msg"));
    }

    @Test
    void logWithNullMessage() {
        LoggerOptions loggerOptions = new LoggerOptions(DefaultLoggerTest.class, tmpDir);
        Logger logger = new DefaultLogger(loggerOptions);

        assertThrows(IllegalArgumentException.class, () -> logger.log(Level.DEBUG, LocalDateTime.now(), null));
    }

    @Test
    void logWithLowerLevel() {
        LoggerOptions loggerOptions = new LoggerOptions(DefaultLoggerTest.class, tmpDir);
        Logger logger = new DefaultLogger(loggerOptions);
        logger.getOptions().setMinLogLevel(Level.WARN);
        logger.getOptions().setShouldThrowErrors(true);

        assertThrows(LogException.class, () -> logger.log(Level.INFO, LocalDateTime.now(), "msg"));
    }

    @Test
    void logWithEmptyMessage() {
        LoggerOptions loggerOptions = new LoggerOptions(DefaultLoggerTest.class, tmpDir);
        Logger logger = new DefaultLogger(loggerOptions);

        assertThrows(IllegalArgumentException.class, () -> logger.log(Level.DEBUG, LocalDateTime.now(), ""));
    }

    @Test
    void logWithLowerThanConfiguredLevelExceptionsAllowed() {
        LoggerOptions loggerOptions = new LoggerOptions(DefaultLoggerTest.class, tmpDir);

        loggerOptions.setMinLogLevel(Level.WARN);
        loggerOptions.setShouldThrowErrors(true);
        Logger logger = new DefaultLogger(loggerOptions);

        assertThrows(LogException.class, () -> logger.log(Level.DEBUG, LocalDateTime.now(), "msg"));
    }

    @Test
    void logWhenDirDoesNotExist() throws IOException {
        Path parentDir = Path.of(tmpDir).resolve("mjt-logs");
        Path childDir = parentDir.resolve("child-dir");

        LoggerOptions loggerOptions = new LoggerOptions(DefaultLoggerTest.class, childDir.toString());
        Logger logger = new DefaultLogger(loggerOptions);

        logger.log(Level.INFO, LocalDateTime.now(), "msg");

        assertTrue(Files.exists(childDir));
        assertTrue(Files.exists(logger.getCurrentFilePath()));
        assertFalse(Files.isDirectory(logger.getCurrentFilePath()));

        Files.deleteIfExists(logger.getCurrentFilePath());
        Files.deleteIfExists(childDir);
        Files.deleteIfExists(parentDir);
    }

    @Test
    void logWhenDirExists() throws IOException {
        Path path = Files.createTempDirectory("mjt-logs");

        LoggerOptions loggerOptions = new LoggerOptions(DefaultLoggerTest.class, path.toString());
        Logger logger = new DefaultLogger(loggerOptions);

        logger.log(Level.INFO, LocalDateTime.now(), "msg");

        assertTrue(Files.exists(path));
        assertTrue(Files.exists(logger.getCurrentFilePath()));
        assertFalse(Files.isDirectory(logger.getCurrentFilePath()));

        Files.deleteIfExists(logger.getCurrentFilePath());
        Files.deleteIfExists(path);
    }

    @Test
    void logFileCreatedSuccessfully() throws IOException {
        Path path = Files.createTempDirectory("mjt-logs");

        LoggerOptions loggerOptions = new LoggerOptions(DefaultLoggerTest.class, path.toString());
        Logger logger = new DefaultLogger(loggerOptions);
        LocalDateTime timestamp = LocalDateTime.now();

        logger.log(Level.INFO, timestamp, "msg");

        File logFile = logger.getCurrentFilePath().toFile();

        assertTrue(logFile.exists());
        assertTrue(logFile.isFile());
        assertTrue(logFile.length() > 0);

        Files.deleteIfExists(logFile.toPath());
        Files.deleteIfExists(path);
    }

    @Test
    void logWrittenToFileSuccessfully() throws IOException {
        Path path = Files.createTempDirectory("mjt-logs");

        LoggerOptions loggerOptions = new LoggerOptions(DefaultLoggerTest.class, path.toString());
        Logger logger = new DefaultLogger(loggerOptions);

        LocalDateTime timestamp = LocalDateTime.now();
        String expectedMessage =
                new Log(Level.INFO,
                        timestamp,
                        DefaultLogger.class.getPackageName(),
                        "msg").toString();

        logger.log(Level.INFO, timestamp, "msg");

        String firstLine = "";
        try (BufferedReader reader = Files.newBufferedReader(logger.getCurrentFilePath())) {
            firstLine = reader.readLine() + System.lineSeparator();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expectedMessage, firstLine);

        Files.deleteIfExists(logger.getCurrentFilePath());
        Files.deleteIfExists(path);
    }

    @Test
    void logWhenAllowedFileSizeReached() throws IOException {
        Path path = Files.createTempDirectory("mjt-logs");

        LoggerOptions loggerOptions = new LoggerOptions(DefaultLoggerTest.class, path.toString());
        loggerOptions.setMaxFileSizeBytes(50);
        Logger logger = new DefaultLogger(loggerOptions);

        logger.log(Level.INFO, LocalDateTime.now(), "msg");
        Path lastLogPath = logger.getCurrentFilePath();

        assertTrue(Files.exists(lastLogPath));
        assertTrue(Files.size(lastLogPath) >= 50);

        logger.log(Level.INFO, LocalDateTime.now(), "msg");
        assertTrue(Files.exists(logger.getCurrentFilePath()));
        assertNotEquals(lastLogPath, logger.getCurrentFilePath());

        Files.deleteIfExists(lastLogPath);
        Files.deleteIfExists(logger.getCurrentFilePath());
        Files.deleteIfExists(path);
    }
}