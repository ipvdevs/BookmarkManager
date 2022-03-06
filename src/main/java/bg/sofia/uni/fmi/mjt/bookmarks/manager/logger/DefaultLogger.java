package bg.sofia.uni.fmi.mjt.bookmarks.manager.logger;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.exception.LogException;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class DefaultLogger implements Logger {
    private static final String LEVEL_VAR_NAME = "Level";
    private static final String TIMESTAMP_VAR_NAME = "Timestamp";
    private static final String MESSAGE_VAR_NAME = "Message";

    private static long logFileId = 0L;

    private final LoggerOptions options;

    public DefaultLogger(LoggerOptions options) {
        this.options = options;
    }

    @Override
    public void log(Level level, LocalDateTime timestamp, String message) {
        CommonValidations.throwIfNull(level, LEVEL_VAR_NAME);
        CommonValidations.throwIfNull(timestamp, TIMESTAMP_VAR_NAME);
        CommonValidations.throwIfNullOrEmpty(message, MESSAGE_VAR_NAME);

        if (level.getLevel() < options.getMinLogLevel().getLevel()) {
            throwLogExceptionIfAllowed("The provided level is lower than the configured minimum log level.");
            return;
        }

        setLogEnvironment();

        Log log = new Log(level, timestamp, options.getClazz().getPackageName(), message);

        Path currentLogFile = this.getCurrentFilePath();

        try (Writer logWriter = Files.newBufferedWriter(
                currentLogFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {

            logWriter.write(log.toString());
            logWriter.flush();

        } catch (IOException e) {
            throwLogExceptionIfAllowed("Failed to log " + log + " in " + currentLogFile);
        }
    }

    private void setLogEnvironment() {
        initLogDirIfNotExists();

        Path currentLogFile = this.getCurrentFilePath();
        if (Files.exists(currentLogFile) && logLimitReached(currentLogFile)) {
            ++logFileId;
        }

        initLogFileIfNotExists();
    }

    /**
     * Checks if the limit size of the current log file is reached.
     *
     * @param logFilePath the current log file path.
     * @return true if the limit size of the current log file is reached, false otherwise.
     */
    private boolean logLimitReached(Path logFilePath) {
        try {
            long fileSize = Files.size(logFilePath);

            return fileSize >= options.getMaxFileSizeBytes();

        } catch (IOException e) {
            throwLogExceptionIfAllowed("An error occurred while trying to get " + logFilePath + "'s size");

            return true;
        }
    }

    /**
     * @param message LogException message
     * @throws LogException if it is allowed in options
     */
    private void throwLogExceptionIfAllowed(String message) {
        if (options.shouldThrowErrors()) {
            throw new LogException(message);
        }
    }

    /**
     * Creates the log directory specified in {@link LoggerOptions} if it does not already exist.
     */
    private void initLogDirIfNotExists() {
        Path logsDirPath = Path.of(options.getDirectory());
        if (!Files.exists(logsDirPath)) {
            try {
                Files.createDirectories(logsDirPath);
            } catch (IOException e) {
                throwLogExceptionIfAllowed("An error occurred while trying to create " + options.getDirectory());
            }
        }
    }

    /**
     * Creates an empty logs-{@link DefaultLogger#logFileId}.txt file if it does not already exist.
     */
    private void initLogFileIfNotExists() {
        Path currentFilePath = this.getCurrentFilePath();
        if (!Files.exists(currentFilePath)) {
            try {
                Files.createFile(currentFilePath);
            } catch (IOException e) {
                throwLogExceptionIfAllowed("An error occurred while trying to create file: " + currentFilePath);
            }
        }
    }

    @Override
    public LoggerOptions getOptions() {
        return options;
    }

    @Override
    public Path getCurrentFilePath() {
        return Path.of(options.getDirectory(), String.format("logs-%d.txt", logFileId));
    }
}
