package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.misc;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.CommandType;

public class UnknownCommand extends Command {
    private String message = "Unknown command. Type help to check the commands usage.";

    public UnknownCommand() {
        super(CommandType.UNKNOWN);
    }

    public UnknownCommand(String message) {
        this();
        this.message = message;
    }

    @Override
    public String execute() {
        return message;
    }
}
