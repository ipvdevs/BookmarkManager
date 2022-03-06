package bg.sofia.uni.fmi.mjt.bookmarks.manager.command;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark.AddToCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark.CleanupCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark.ImportChromeCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark.ListCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark.NewGroupCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark.RemoveFromCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark.SearchCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.misc.HelpCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.misc.UnknownCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.LoginCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.LogoutCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.RegisterCommand;

import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class CommandParser {
    private static final String WHITESPACE_PATTERN = "\\s+";

    private static final int COMMAND_TOKEN_ID = 0;

    private CommandParser() {
    }

    public static Command of(String line, SocketChannel sc) {
        if (line == null || sc == null) {
            return unknown();
        }

        String[] tokens = line.split(WHITESPACE_PATTERN);

        tokens[0] = tokens[0].toLowerCase();

        CommandType type = matchCommandType(tokens[COMMAND_TOKEN_ID]);

        return switch (type) {
            case HELP -> parseHelp(tokens);
            case REGISTER -> parseRegister(tokens);
            case LOGIN -> parseLogin(sc, tokens);
            case LOGOUT -> parseLogout(sc, tokens);
            case NEW_GROUP -> parseNewGroup(sc, tokens);
            case ADD_TO -> parseAddTo(sc, tokens);
            case REMOVE_FROM -> parseRemoveFrom(sc, tokens);
            case LIST -> parseList(sc, tokens);
            case SEARCH -> parseSearch(sc, tokens);
            case CLEANUP -> parseCleanup(sc, tokens);
            case IMPORT_FROM_CHROME -> parseImportFromChrome(sc, tokens);
            case UNKNOWN -> unknown();
        };
    }

    private static Command parseRemoveFrom(SocketChannel sc, String[] tokens) {
        final int removeFromTokensLength = 3;

        if (validateTokens(tokens, removeFromTokensLength)) {
            return new RemoveFromCommand(sc, tokens);
        }

        return unknown();
    }

    private static Command parseList(SocketChannel sc, String[] tokens) {
        final int listTokensLength = 1;

        if (validateTokens(tokens, listTokensLength)) {
            return new ListCommand(sc);
        }

        final int listTokensGroupLength = 3;
        final int flagId = 1;

        if (validateTokens(tokens, listTokensGroupLength) &&
            tokens[flagId].equals("--group-name")) {

            ListCommand commandList = new ListCommand(sc, tokens);

            commandList.groupFlag(true);

            return commandList;
        }

        return unknown();
    }

    private static Command parseAddTo(SocketChannel sc, String[] tokens) {
        final int addToTokensLength = 3;
        final int addToShortenTokensLength = 4;


        if (validateTokens(tokens, addToTokensLength) &&
            !tokens[2].equals("--shorten")) {
            return new AddToCommand(sc, tokens);
        }

        final int flagId = 3;
        if (validateTokens(tokens, addToShortenTokensLength) &&
            tokens[flagId].equals("--shorten")) {
            AddToCommand addToShorten = new AddToCommand(sc, tokens);
            addToShorten.setShorten(true);

            return addToShorten;
        }

        return unknown();
    }

    private static Command parseNewGroup(SocketChannel sc, String[] tokens) {
        final int newGroupTokensLength = 2;

        if (validateTokens(tokens, newGroupTokensLength)) {
            return new NewGroupCommand(sc, tokens);
        }

        return unknown();
    }

    private static Command parseSearch(SocketChannel sc, String[] tokens) {
        final int searchTokensLength = 3;
        final int flagTokenId = 1;

        if (validateTokens(tokens, searchTokensLength) &&
            tokens[flagTokenId].equals("--title")) {
            SearchCommand searchCommand = new SearchCommand(sc, tokens);

            searchCommand.titleFlag(true);

            return searchCommand;
        }

        if (tokens != null && tokens.length >= searchTokensLength &&
            tokens[flagTokenId].equals("--tags")) {
            SearchCommand searchCommand = new SearchCommand(sc, tokens);

            searchCommand.tagsFlag(true);

            return searchCommand;
        }

        return unknown();
    }

    private static Command parseCleanup(SocketChannel sc, String[] tokens) {
        final int cleanupTokensLength = 1;

        if (validateTokens(tokens, cleanupTokensLength)) {
            return new CleanupCommand(sc);
        }

        return unknown();
    }

    private static Command parseImportFromChrome(SocketChannel sc, String[] tokens) {
        final int importTokensLength = 1;

        if (validateTokens(tokens, importTokensLength)) {
            return new ImportChromeCommand(sc, tokens);
        }

        return unknown();
    }

    private static Command parseRegister(String[] tokens) {
        final int registerTokensLength = 3;

        if (validateTokens(tokens, registerTokensLength)) {
            return new RegisterCommand(tokens);
        }

        return unknown();
    }

    private static Command parseLogin(SocketChannel sc, String[] tokens) {
        final int loginTokensLength = 3;

        if (validateTokens(tokens, loginTokensLength)) {
            return new LoginCommand(sc, tokens);
        }

        return unknown();
    }

    private static Command parseLogout(SocketChannel sc, String[] tokens) {
        final int logoutTokensLength = 1;

        if (validateTokens(tokens, logoutTokensLength)) {
            return new LogoutCommand(sc);
        }

        return unknown();
    }

    private static Command parseHelp(String[] tokens) {
        final int helpTokensLength = 1;

        if (tokens == null || tokens.length != helpTokensLength) {
            return unknown();
        }

        return new HelpCommand();
    }


    private static Command unknown() {
        return new UnknownCommand();
    }

    private static CommandType matchCommandType(String command) {
        return Arrays.stream(CommandType.values())
                .filter(type -> command.equalsIgnoreCase(type.getName()))
                .findFirst()
                .orElse(CommandType.UNKNOWN);
    }

    private static boolean validateTokens(String[] tokens, int expectedLength) {
        return tokens != null && tokens.length == expectedLength;
    }
}
