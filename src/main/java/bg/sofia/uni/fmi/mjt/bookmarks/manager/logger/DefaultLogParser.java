package bg.sofia.uni.fmi.mjt.bookmarks.manager.logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultLogParser implements LogParser {
    private final Path logsFilePath;

    public DefaultLogParser(Path logsFilePath) {
        this.logsFilePath = logsFilePath;
    }

    @Override
    public List<Log> getLogs(Level level) {
        CommonValidations.throwIfNull(level, "Level");

        List<Log> logsByLevel = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(logsFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\|");

                Log log = parseLog(tokens);

                if (log.level() == level) {
                    logsByLevel.add(log);
                }

            }

        } catch (IOException e) {
            return Collections.emptyList();
        }

        return logsByLevel;
    }

    @Override
    public List<Log> getLogs(LocalDateTime from, LocalDateTime to) {
        CommonValidations.throwIfNull(from, "From");
        CommonValidations.throwIfNull(to, "To");

        List<Log> logsByDateRange = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(logsFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\|");

                Log log = parseLog(tokens);

                if (log.timestamp().isAfter(from) && log.timestamp().isBefore(to)) {
                    logsByDateRange.add(log);
                }

                if (log.timestamp().isEqual(from) || log.timestamp().isEqual(to)) {
                    logsByDateRange.add(log);
                }
            }

        } catch (IOException e) {
            return Collections.emptyList();
        }

        return logsByDateRange;
    }

    @Override
    public List<Log> getLogsTail(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("A non-negative number must be used as argument");
        }

        if (n == 0) {
            return Collections.emptyList();
        }

        List<Log> logs = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(logsFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\|");

                Log log = parseLog(tokens);

                logs.add(log);
            }

        } catch (IOException e) {
            return Collections.emptyList();
        }


        if (logs.size() <= n) {
            return logs;
        }

        return logs.subList(logs.size() - n, logs.size());
    }

    private Log parseLog(String[] tokens) {
        final int levelToken = 0;
        final int timeToken = 1;
        final int packageToken = 2;
        final int messageToken = 3;

        String levelString = tokens[levelToken].substring(1, tokens[levelToken].length() - 1);
        Level level = Level.parseLevel(levelString);

        LocalDateTime timestamp = LocalDateTime.parse(tokens[timeToken]);

        String packageName = tokens[packageToken];
        String message = tokens[messageToken];

        return new Log(level, timestamp, packageName, message);
    }
}
