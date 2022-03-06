package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.LoginCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.RegisterCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchCommandTest {
    @Test
    void searchUnauthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        Command search = new SearchCommand(client);

        assertEquals(
                "Please login to use this feature." + System.lineSeparator() +
                "If you don't have account use the register command"
                , search.execute(),
                "Should deny access for unauthorised users."
        );
    }

    @Test
    void searchByTagAuthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "searchExec";
        String password = "R3@llY$tr0ng";

        Command register = new RegisterCommand("register", username, password);
        Command login = new LoginCommand(client, "login", username, password);
        SearchCommand searchByTag = new SearchCommand(client, "arg1", "arg2", "arg3");
        searchByTag.tagsFlag(true);

        register.execute();
        login.execute();

        String expected = """
                          ------------------------------
                          [arg3]:\s
                          ------------------------------
                          """;

        assertEquals(
                expected
                , searchByTag.execute(),
                "Should be able to access the search feature"
        );
    }

    @Test
    void searchByTitleAuthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "searchExec";
        String password = "R3@llY$tr0ng";

        Command register = new RegisterCommand("register", username, password);
        Command login = new LoginCommand(client, "login", username, password);
        SearchCommand searchByTitle = new SearchCommand(client, "arg1", "arg2", "arg3");
        searchByTitle.titleFlag(true);

        register.execute();
        login.execute();

        String expected = """
                          ------------------------------
                          arg3:\s
                          ------------------------------
                          """;

        assertEquals(
                expected
                , searchByTitle.execute(),
                "Should be able to access the search feature"
        );
    }
}