package bg.sofia.uni.fmi.mjt.bookmarks.manager.logger;

public enum Level {
    ERROR(4), WARN(3), INFO(2), DEBUG(1);

    private final int level;

    Level(int level) {
        this.level = level;
    }

    int getLevel() {
        return level;
    }

    public static Level parseLevel(String level) {
        return switch (level) {
            case "ERROR" -> ERROR;
            case "WARN" -> WARN;
            case "INFO" -> INFO;
            case "DEBUG" -> DEBUG;
            default -> throw new IllegalArgumentException("Invalid level: " + level);
        };
    }
}