package bg.sofia.uni.fmi.mjt.bookmarks.manager.command;

import java.util.List;

public abstract class Command {
    protected final CommandType type;
    protected final List<String> args;

    public Command(CommandType type, String... args) {
        this.type = type;
        this.args = List.of(args);
    }

    /**
     * Executes a given command.
     *
     * @return The response in human-readable format ready for the user.
     */
    public abstract String execute();

    public CommandType getType() {
        return type;
    }

    public List<String> getArgs() {
        return args;
    }
}
