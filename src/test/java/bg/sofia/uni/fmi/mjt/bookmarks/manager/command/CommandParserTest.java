package bg.sofia.uni.fmi.mjt.bookmarks.manager.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CommandParserTest {
    private final SocketChannel sc = Mockito.mock(SocketChannel.class);

    @Test
    public void testCommandParseWithSingleInvalidCommand() {
        String line = "test";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.UNKNOWN, cmd.getType(), "A command " + line + " does not exist.");
    }

    @Test
    public void testCommandParseWithInvalidCommand() {
        String line = "Lorem Ipsum --args";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.UNKNOWN, cmd.getType(), "A command " + line + " does not exist.");
    }

    @Test
    public void testCommandParseWithNullLine() {
        Command cmd = CommandParser.of(null, sc);

        assertEquals(CommandType.UNKNOWN, cmd.getType(), "A command with null line should be parsed to unknown.");
    }

    @Test
    public void testCommandParseWithNullSocketChannel() {
        String line = "help";

        Command cmd = CommandParser.of(line, null);

        assertEquals(CommandType.UNKNOWN, cmd.getType(), "A command with null SocketChannel should be parsed to unknown.");
    }

    @Test
    public void testHelpCommandInvalidParse() {
        String line = "help me";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.UNKNOWN, cmd.getType(), "The line does not match the exact help command invocation string");
    }

    @Test
    public void testHelpCommandParse() {
        String line = "help";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.HELP, cmd.getType(), "The command help exists and it's valid.");
        assertTrue(cmd.getArgs().isEmpty(), "No args should be provided for <help>.");
    }

    @Test
    public void testHelpCommandParseWithWhitespaces() {
        String line = "help\t\t  ";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.HELP, cmd.getType(), "The command help exists and it's valid. " +
                                                      "Whitespaces should be considered when parsing.");
        assertTrue(cmd.getArgs().isEmpty(), "No args should be provided for <help>.");
    }

    @Test
    public void testHelpCommandCaseSensitivityParse() {
        String line = "HElp";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.HELP, cmd.getType(), "The command help exists and it's valid." +
                                                      " No case-sensitivity should be included.");
        assertTrue(cmd.getArgs().isEmpty(), "No args should be provided to help.");
    }

    @Test
    public void testRegisterCommandParse() {
        String line = "register username password";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.REGISTER, cmd.getType(), "The command register exists and it's valid.")
        ;
        assertEquals(3, cmd.getArgs().size(), "Three arguments should be passed (register, <username>, <password>).");
        assertEquals("register", cmd.getArgs().get(0), "The first argument is the command.");
        assertEquals("username", cmd.getArgs().get(1), "The second argument is the username.");
        assertEquals("password", cmd.getArgs().get(2), "The third argument is the password.");
    }

    @Test
    public void testRegisterCommandCaseSensitivityParse() {
        String line = "ReGiStER username password";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.REGISTER, cmd.getType(), "The command register exists and it's valid. " +
                                                          "No case-sensitivity should be included.");

        assertEquals(3, cmd.getArgs().size(), "Three arguments should be passed (register, <username>, <password>).");
        assertEquals("register", cmd.getArgs().get(0), "The first argument is the command.");
        assertEquals("username", cmd.getArgs().get(1), "The second argument is the username.");
        assertEquals("password", cmd.getArgs().get(2), "The third argument is the password.");
    }

    @Test
    public void testRegisterCommandInvalidParse() {
        String message = "The line does not match the exact \"register <username> <password>\" command invocation string";
        String line1 = "register username";
        String line2 = "register";
        String line3 = "register 1 2 3";

        Command cmd1 = CommandParser.of(line1, sc);
        Command cmd2 = CommandParser.of(line2, sc);
        Command cmd3 = CommandParser.of(line3, sc);

        assertEquals(CommandType.UNKNOWN, cmd1.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd2.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd3.getType(), message);
    }

    @Test
    public void testLoginCommandParse() {
        String line = "login username password";


        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.LOGIN, cmd.getType(), "The command login exists and it's valid.");

        assertEquals(3, cmd.getArgs().size(), "Three arguments should be passed (login, <username>, <password>).");
        assertEquals("login", cmd.getArgs().get(0), "The first argument is the command - login.");
        assertEquals("username", cmd.getArgs().get(1), "The second argument is the username.");
        assertEquals("password", cmd.getArgs().get(2), "The third argument is the password.");
    }

    @Test
    public void testLoginCommandCaseSensitivityParse() {
        String line = "LoGiN username password";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.LOGIN, cmd.getType(), "The command login exists and it's valid." +
                                                       "No case-sensitivity should be included.");

        assertEquals(3, cmd.getArgs().size(), "Three arguments should be passed.");
        assertEquals("login", cmd.getArgs().get(0), "The first argument is the command - login.");
        assertEquals("username", cmd.getArgs().get(1), "The second argument is the username.");
        assertEquals("password", cmd.getArgs().get(2), "The third argument is the password.");
    }

    @Test
    public void testLoginCommandInvalidParse() {
        String message = "The line does not match the exact \"login <username> <password>\" command invocation string";
        String line1 = "login username";
        String line2 = "login";
        String line3 = "login 1 2 3";

        Command cmd1 = CommandParser.of(line1, sc);
        Command cmd2 = CommandParser.of(line2, sc);
        Command cmd3 = CommandParser.of(line3, sc);

        assertEquals(CommandType.UNKNOWN, cmd1.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd2.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd3.getType(), message);
    }

    @Test
    public void testLogoutCommandParse() {
        String line = "logout";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.LOGOUT, cmd.getType(), "The command logout exists and it's valid.");
        assertTrue(cmd.getArgs().isEmpty(), "No arguments are needed for logout.");
    }

    @Test
    public void testLogoutCommandCaseSensitivityParse() {
        String line = "LoGoUt";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.LOGOUT, cmd.getType(), "The command logout exists and it's valid." +
                                                        "No case-sensitivity should be included.");
        assertTrue(cmd.getArgs().isEmpty(), "No arguments are needed for logout.");
    }

    @Test
    public void testLogoutCommandInvalidParse() {
        String message = "The line does not match the exact \"logout\" command invocation string";
        String line1 = "logout test1";
        String line2 = "logout test1 test2";

        Command cmd1 = CommandParser.of(line1, sc);
        Command cmd2 = CommandParser.of(line2, sc);

        assertEquals(CommandType.UNKNOWN, cmd1.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd2.getType(), message);
    }

    @Test
    public void testNewGroupCommandParse() {
        String line = "new-group name";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.NEW_GROUP, cmd.getType(), "The command new-group exists and it's valid.");

        assertEquals(2, cmd.getArgs().size(), "Two arguments should be passed (new-group, <group-name>).");
        assertEquals("new-group", cmd.getArgs().get(0), "The first argument is the command - new-group.");
        assertEquals("name", cmd.getArgs().get(1), "The second argument is the group-name (name).");
    }

    @Test
    public void testNewGroupCommandCaseSensitivityParse() {
        String line = "new-GROUP name";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.NEW_GROUP, cmd.getType(), "The command new-group exists and it's valid." +
                                                           "No case-sensitivity should be included.");

        assertEquals(2, cmd.getArgs().size(), "Two arguments should be passed (new-group, <group-name>).");
        assertEquals("new-group", cmd.getArgs().get(0), "The first argument is the command - new-group.");
        assertEquals("name", cmd.getArgs().get(1), "The second argument is the group-name (name).");
    }

    @Test
    public void testNewGroupCommandInvalidParse() {
        String message = "The line does not match the exact \"new-group <group-name>\" command invocation string";
        String line1 = "new-group";
        String line2 = "new-group One Two";

        Command cmd1 = CommandParser.of(line1, sc);
        Command cmd2 = CommandParser.of(line2, sc);

        assertEquals(CommandType.UNKNOWN, cmd1.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd2.getType(), message);
    }

    @Test
    public void testAddToCommandParse() {
        String line = "add-to name url";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.ADD_TO, cmd.getType(), "The command add-to exists and it's valid.");

        assertEquals(3, cmd.getArgs().size(), "Three arguments should be passed (add-to <group-name> <bookmark>).");
        assertEquals("add-to", cmd.getArgs().get(0), "The first argument is the command - add-to.");
        assertEquals("name", cmd.getArgs().get(1), "The second argument is the group-name (name).");
        assertEquals("url", cmd.getArgs().get(2), "The third argument is the bookmark (url).");
    }

    @Test
    public void testAddToCommandCaseSensitivityParse() {
        String line = "ADD-To name url";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.ADD_TO, cmd.getType(), "The command add-to exists and it's valid.");

        assertEquals(3, cmd.getArgs().size(), "Three arguments should be passed (add-to <group-name> <bookmark>).");
        assertEquals("add-to", cmd.getArgs().get(0), "The first argument is the command - add-to.");
        assertEquals("name", cmd.getArgs().get(1), "The second argument is the group-name (name).");
        assertEquals("url", cmd.getArgs().get(2), "The third argument is the bookmark (url).");
    }

    @Test
    public void testAddToCommandInvalidParse() {
        String message = "The line does not match the exact \"add-to <group-name> <bookmark>\" command invocation string";
        String line1 = "add-to";
        String line2 = "add-to name";
        String line3 = "add-to name url test";

        Command cmd1 = CommandParser.of(line1, sc);
        Command cmd2 = CommandParser.of(line2, sc);
        Command cmd3 = CommandParser.of(line3, sc);

        assertEquals(CommandType.UNKNOWN, cmd1.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd2.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd3.getType(), message);
    }

    @Test
    public void testAddToShortenCommandParse() {
        String line = "add-to name url --shorten";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.ADD_TO, cmd.getType(), "The command add-to exists and it's valid. The flag --shorten is set.");

        assertEquals(4, cmd.getArgs().size(), "Four arguments should be passed (add-to <group-name> <bookmark> --shorten).");
        assertEquals("add-to", cmd.getArgs().get(0), "The first argument is the command - add-to.");
        assertEquals("name", cmd.getArgs().get(1), "The second argument is the group-name (name).");
        assertEquals("url", cmd.getArgs().get(2), "The third argument is the bookmark (url).");
        assertEquals("--shorten", cmd.getArgs().get(3), "The third argument is the bookmark (url).");
    }

    @Test
    public void testAddToShortenCommandCaseSensitivityParse() {
        String line = "ADD-TO name url --shorten";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.ADD_TO, cmd.getType(), "The command add-to exists and it's valid. The flag --shorten is set.");

        assertEquals(4, cmd.getArgs().size(), "Four arguments should be passed (add-to <group-name> <bookmark> --shorten).");
        assertEquals("add-to", cmd.getArgs().get(0), "The first argument is the command - add-to.");
        assertEquals("name", cmd.getArgs().get(1), "The second argument is the group-name (name).");
        assertEquals("url", cmd.getArgs().get(2), "The third argument is the bookmark (url).");
        assertEquals("--shorten", cmd.getArgs().get(3), "The third argument is the bookmark (url).");
    }

    @Test
    public void testAddToShortenToCommandInvalidParse() {
        String message = "The line does not match the exact \"add-to <group-name> <bookmark> --shorten\" command invocation string";
        String line1 = "add-to --shorten";
        String line2 = "add-to name --shorten";
        String line3 = "add-to name url test --shorten";
        String line4 = "add-to name url --shorten test";

        Command cmd1 = CommandParser.of(line1, sc);
        Command cmd2 = CommandParser.of(line2, sc);
        Command cmd3 = CommandParser.of(line3, sc);
        Command cmd4 = CommandParser.of(line4, sc);

        assertEquals(CommandType.UNKNOWN, cmd1.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd2.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd3.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd4.getType(), message);
    }

    @Test
    public void testRemoveFromCommandParse() {
        String line = "remove-from name url";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.REMOVE_FROM, cmd.getType(), "The command remove-to exists and it's valid.");

        assertEquals(3, cmd.getArgs().size(), "Three arguments should be passed (remove-from <group-name> <bookmark>).");
        assertEquals("remove-from", cmd.getArgs().get(0), "The first argument is the command - remove-from.");
        assertEquals("name", cmd.getArgs().get(1), "The second argument is the group-name (name).");
        assertEquals("url", cmd.getArgs().get(2), "The third argument is the bookmark (url).");
    }

    @Test
    public void testRemoveFromCommandCaseSensitivityParse() {
        String line = "REMOVE-from name url";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.REMOVE_FROM, cmd.getType(), "The command add-to exists and it's valid." +
                                                             "No case-sensitivity should be included.");

        assertEquals(3, cmd.getArgs().size(), "Three arguments should be passed (remove-from <group-name> <bookmark>).");
        assertEquals("remove-from", cmd.getArgs().get(0), "The first argument is the command - remove-from.");
        assertEquals("name", cmd.getArgs().get(1), "The second argument is the group-name (name).");
        assertEquals("url", cmd.getArgs().get(2), "The third argument is the bookmark (url).");
    }

    @Test
    public void testRemoveFromCommandInvalidParse() {
        String message = "The line does not match the exact \"remove-from <group-name> <bookmark>\" command invocation string";
        String line1 = "remove-from";
        String line2 = "remove-from name";
        String line3 = "remove-from name url test";

        Command cmd1 = CommandParser.of(line1, sc);
        Command cmd2 = CommandParser.of(line2, sc);
        Command cmd3 = CommandParser.of(line3, sc);

        assertEquals(CommandType.UNKNOWN, cmd1.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd2.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd3.getType(), message);
    }

    @Test
    public void testListCommand() {
        String line = "list";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.LIST, cmd.getType(), "The command list exists and it's valid.");
        assertTrue(cmd.getArgs().isEmpty(), "No args should be provided for <list>");
    }

    @Test
    public void testListCommandCaseSensitivity() {
        String line = "LIst";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.LIST, cmd.getType(), "The command list exists and it's valid." +
                                                      "No case-sensitivity should be included.");
        assertTrue(cmd.getArgs().isEmpty(), "No args should be provided for <list>");
    }

    @Test
    public void testListCommandInvalid() {
        String message = "The line does not match the exact \"remove-from <group-name> <bookmark>\" command invocation string";
        String line1 = "list-it";
        String line2 = "list all";
        String line3 = "list a b c";

        Command cmd1 = CommandParser.of(line1, sc);
        Command cmd2 = CommandParser.of(line2, sc);
        Command cmd3 = CommandParser.of(line3, sc);

        assertEquals(CommandType.UNKNOWN, cmd1.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd2.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd3.getType(), message);
    }

    @Test
    public void testListByGroupCommand() {
        String line = "list --group-name groupName";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.LIST, cmd.getType(), "The command list exists and it's valid. The flag --group-name is set.");

        assertEquals(cmd.getArgs().get(0), "list", "The first argument is the command - list.");
        assertEquals(cmd.getArgs().get(1), "--group-name", "The second argument is the flag --group-name.");
        assertEquals(cmd.getArgs().get(2), "groupName", "The third argument is the <group-name>");
    }

    @Test
    public void testListByGroupCommandCaseSensitivity() {
        String line = "List --group-name groupName";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.LIST, cmd.getType(), "The command list exists and it's valid." +
                                                      "No case-sensitivity should be included.");

        assertEquals(cmd.getArgs().get(0), "list", "The first argument is the command - list.");
        assertEquals(cmd.getArgs().get(1), "--group-name", "The second argument is the flag --group-name.");
        assertEquals(cmd.getArgs().get(2), "groupName", "The third argument is the <group-name>");
    }

    @Test
    public void testListByGroupCommandInvalid() {
        String message = "The line does not match the exact \"list --group-name <group-name>\" command invocation string";
        String line1 = "list --group-name";
        String line2 = "list --group-name asd asd";

        Command cmd1 = CommandParser.of(line1, sc);
        Command cmd2 = CommandParser.of(line2, sc);

        assertEquals(CommandType.UNKNOWN, cmd1.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd2.getType(), message);
    }

    @Test
    public void testSearchByTitleCommand() {
        String line = "search --title title";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.SEARCH, cmd.getType(), "The command search exists and it's valid. The flag --title is set.");

        assertEquals(cmd.getArgs().get(0), "search", "The first argument is the command - search.");
        assertEquals(cmd.getArgs().get(1), "--title", "The second argument is the flag --title.");
        assertEquals(cmd.getArgs().get(2), "title", "The third argument is the <title>");
    }

    @Test
    public void testSearchByTitleCommandCaseSensitivity() {
        String line = "SEARCH --title title";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.SEARCH, cmd.getType(), "The command search exists and it's valid. The flag --title is set.");

        assertEquals(cmd.getArgs().get(0), "search", "The first argument is the command - search.");
        assertEquals(cmd.getArgs().get(1), "--title", "The second argument is the flag --title.");
        assertEquals(cmd.getArgs().get(2), "title", "The third argument is the <title>");
    }

    @Test
    public void testSearchByTitleCommandInvalid() {
        String message = "The line does not match the exact \"search --title <title>\" command invocation string";
        String line1 = "search --title";
        String line2 = "search --title one two";

        Command cmd1 = CommandParser.of(line1, sc);
        Command cmd2 = CommandParser.of(line2, sc);

        assertEquals(CommandType.UNKNOWN, cmd1.getType(), message);
        assertEquals(CommandType.UNKNOWN, cmd2.getType(), message);
    }

    @Test
    public void testSearchByTagsCommand() {
        String line = "search --tags title";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.SEARCH, cmd.getType(), "The command search exists and it's valid. The flag --tags is set.");

        assertEquals(cmd.getArgs().get(0), "search", "The first argument is the command - search.");
        assertEquals(cmd.getArgs().get(1), "--tags", "The second argument is the flag --tags.");
        assertEquals(cmd.getArgs().get(2), "title", "The third argument is the <title>");
    }

    @Test
    public void testSearchByTagsCommandCaseSensitivity() {
        String line = "SEARCH --tags title";

        Command cmd = CommandParser.of(line, sc);

        assertEquals(CommandType.SEARCH, cmd.getType(), "The command search exists and it's valid. The flag --tags is set.");

        assertEquals(cmd.getArgs().get(0), "search", "The first argument is the command - search.");
        assertEquals(cmd.getArgs().get(1), "--tags", "The second argument is the flag --tags.");
        assertEquals(cmd.getArgs().get(2), "title", "The third argument is the <title>");
    }

    @Test
    public void testSearchByTagsCommandInvalid() {
        String message = "The line does not match the exact \"search --tags <title>\" command invocation string";
        String line1 = "search --tags";

        Command cmd1 = CommandParser.of(line1, sc);

        assertEquals(CommandType.UNKNOWN, cmd1.getType(), message);
    }
}