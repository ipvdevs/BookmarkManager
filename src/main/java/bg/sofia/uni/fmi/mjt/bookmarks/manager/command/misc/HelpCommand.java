package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.misc;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.CommandType;

public class HelpCommand extends Command {
    private static final String INSTRUCTIONS = "register <username> <password> " + System.lineSeparator() +
                                               "login <username> <password> " + System.lineSeparator() +
                                               "logout " + System.lineSeparator() +
                                               "new-group <group-name> " + System.lineSeparator() +
                                               "add-to <group-name> <bookmark> {--shorten} " + System.lineSeparator() +
                                               "remove-from <group-name> <bookmark> " + System.lineSeparator() +
                                               "list " + System.lineSeparator() +
                                               "list --group-name <group-name>" + System.lineSeparator() +
                                               "search --tags <tag> [<tag> ...] " + System.lineSeparator() +
                                               "search --title <title> " + System.lineSeparator() +
                                               "cleanup " + System.lineSeparator() +
                                               "import-from-chrome " + System.lineSeparator() +
                                               "toggle-prompt";

    public HelpCommand() {
        super(CommandType.HELP);
    }

    @Override
    public String execute() {
        return INSTRUCTIONS;
    }
}
