package bg.sofia.uni.fmi.mjt.bookmarks.manager.logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultLogParserTest {
    private final static LocalDateTime START_DATE = LocalDateTime.now();
    private final static LocalDateTime END_DATE = START_DATE.plusDays(10);

    private static int logId = 0;
    private static int debugLogs = 5;
    private static int infoLogs = 6;
    private static int warnLogs = 7;
    private static int errorLogs = 8;

    private static Path logFile;

    @BeforeAll
    static void initTempEnvironment() throws IOException {
        Path tempDir = Files.createTempDirectory("mjt-logs");

        LoggerOptions options = new LoggerOptions(DefaultLogParserTest.class, tempDir.toString());
        options.setMaxFileSizeBytes(5000);
        options.setMinLogLevel(Level.DEBUG);
        options.setShouldThrowErrors(true);

        Logger logger = new DefaultLogger(options);

        BiConsumer<Integer, Level> addLevelTestLogs = (count, level) -> {
            for (int i = 0; i < count; i++) {
                logger.log(level, LocalDateTime.now(), Integer.toString(logId++));
            }
        };

        addLevelTestLogs.accept(debugLogs, Level.DEBUG);
        addLevelTestLogs.accept(infoLogs, Level.INFO);
        addLevelTestLogs.accept(warnLogs, Level.WARN);
        addLevelTestLogs.accept(errorLogs, Level.ERROR);

        LocalDateTime current = START_DATE;

        while (!current.isEqual(END_DATE)) {
            logger.log(Level.INFO, current, Integer.toString(logId++));
            current = current.plusDays(1);
            ++infoLogs;
        }

        logger.log(Level.INFO, current, Integer.toString(logId++));
        ++infoLogs;

        logFile = logger.getCurrentFilePath();
    }

    @AfterAll
    static void cleanUpTempEnvironment() throws IOException {
        Files.deleteIfExists(logFile);
        Files.deleteIfExists(logFile.getParent());
    }

    @Test
    void getLogsByLevelWithNullLevel() {
        LogParser logParser = new DefaultLogParser(logFile);

        assertThrows(IllegalArgumentException.class, () -> logParser.getLogs(null));
    }

    @Test
    void getLogsByLevelWithInvalidFile() {
        Path corruptedFile = logFile.getParent().resolve("Invalid.tmp");

        LogParser logParser = new DefaultLogParser(corruptedFile);

        assertTrue(logParser.getLogs(Level.DEBUG).isEmpty());
    }

    @Test
    void getLogsByLevel() {
        LogParser logParser = new DefaultLogParser(logFile);

        List<Log> debug = logParser.getLogs(Level.DEBUG);
        List<Log> info = logParser.getLogs(Level.INFO);
        List<Log> warn = logParser.getLogs(Level.WARN);
        List<Log> error = logParser.getLogs(Level.ERROR);

        assertEquals(debugLogs, debug.size());
        assertEquals(infoLogs, info.size());
        assertEquals(warnLogs, warn.size());
        assertEquals(errorLogs, error.size());

        assertTrue(debug.stream().map(Log::level).allMatch(debug.get(0).level()::equals));
        assertTrue(info.stream().map(Log::level).allMatch(info.get(0).level()::equals));
        assertTrue(warn.stream().map(Log::level).allMatch(warn.get(0).level()::equals));
        assertTrue(error.stream().map(Log::level).allMatch(error.get(0).level()::equals));
    }

    @Test
    void getLogsByDateWithNullArguments() {
        LogParser logParser = new DefaultLogParser(logFile);

        assertThrows(IllegalArgumentException.class, () -> logParser.getLogs(null, LocalDateTime.now()));
        assertThrows(IllegalArgumentException.class, () -> logParser.getLogs(LocalDateTime.now(), null));
    }

    @Test
    void getLogsByDateWithInvalidFile() {
        Path corruptedFile = logFile.getParent().resolve("Invalid.tmp");

        LogParser logParser = new DefaultLogParser(corruptedFile);

        assertTrue(logParser.getLogs(LocalDateTime.now(), LocalDateTime.now()).isEmpty());
    }

    @Test
    void getLogsByDate() {
        LogParser logParser = new DefaultLogParser(logFile);

        List<Log> logsByDate = logParser.getLogs(START_DATE.plusDays(1), END_DATE.minusDays(1));
        List<Log> singleStartLogByDate = logParser.getLogs(START_DATE, START_DATE);
        List<Log> middleLogsByDate = logParser.getLogs(START_DATE.plusDays(5), START_DATE.plusDays(6));
        List<Log> singleEndLogByDate = logParser.getLogs(END_DATE, END_DATE);

        assertEquals(9, logsByDate.size());
        assertEquals(2, middleLogsByDate.size());
        assertEquals(1, singleStartLogByDate.size());
        assertEquals(1, singleEndLogByDate.size());

        assertEquals(START_DATE.plusDays(1), logsByDate.get(0).timestamp());
        assertEquals(END_DATE.minusDays(1), logsByDate.get(logsByDate.size() - 1).timestamp());
        assertEquals(START_DATE, singleStartLogByDate.get(0).timestamp());
        assertEquals(END_DATE, singleEndLogByDate.get(0).timestamp());
        assertEquals(START_DATE.plusDays(5), middleLogsByDate.get(0).timestamp());
        assertEquals(START_DATE.plusDays(6), middleLogsByDate.get(1).timestamp());
    }

    @Test
    void getLogsTailWithNegativeN() {
        LogParser logParser = new DefaultLogParser(logFile);

        assertThrows(IllegalArgumentException.class, () -> logParser.getLogsTail(-5));
    }

    @Test
    void getLogsTailWithZeroN() {
        LogParser logParser = new DefaultLogParser(logFile);

        assertTrue(logParser.getLogsTail(0).isEmpty());
    }

    @Test
    void getLogsTailWithInvalidFile() {
        Path corruptedFile = logFile.getParent().resolve("Invalid.tmp");

        LogParser logParser = new DefaultLogParser(corruptedFile);

        assertTrue(logParser.getLogsTail(5).isEmpty());
    }

    @Test
    void getLogsTailWithLargerNThanAvailableLogs() {
        LogParser logParser = new DefaultLogParser(logFile);
        int availableLogs = debugLogs + infoLogs + warnLogs + errorLogs;
        int largeN = availableLogs + 1000;

        List<Log> tail = logParser.getLogsTail(largeN);

        assertEquals(availableLogs, tail.size());
    }

    @Test
    void getLogsTailWithExactN() {
        LogParser logParser = new DefaultLogParser(logFile);
        int availableLogs = debugLogs + infoLogs + warnLogs + errorLogs;

        List<Log> tail = logParser.getLogsTail(availableLogs);

        assertEquals(availableLogs, tail.size());
    }

    @Test
    void getLogsTail() {
        LogParser logParser = new DefaultLogParser(logFile);

        List<Log> tail = logParser.getLogsTail(5);

        assertEquals(5, tail.size());

        int lastId = logId - 1;
        for (int i = tail.size() - 1; i >= 0; i--) {
            assertEquals(Integer.toString(lastId), tail.get(i).message());
            --lastId;
        }
    }

}