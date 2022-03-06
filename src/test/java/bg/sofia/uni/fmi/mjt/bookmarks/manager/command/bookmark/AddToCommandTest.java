package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.LoginCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.RegisterCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddToCommandTest {

    @Test
    void addToCommandUnauthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        Command addTo = new AddToCommand(client, "add-to", "addToCommandExecution", "https://google.com");

        assertEquals(
                "Please login to use this feature." + System.lineSeparator() +
                "If you don't have account use the register command"
                , addTo.execute(),
                "Should deny access for unauthorised users."
        );
    }

    @Test
    void addToCommandAuthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "addToExec";
        String password = "R3@llY$tr0ng";

        Command register = new RegisterCommand("register", username, password);
        Command login = new LoginCommand(client, "login", username, password);
        Command addTo = new AddToCommand(client, "add-to", "addToCommandExecution", "https://google.com");

        register.execute();
        login.execute();

        assertEquals(
                "A group with name addToCommandExecution does not exist."
                , addTo.execute(),
                "Should be able to access the add-to feature"
        );
    }

    @Test
    void addToShortenCommandUnauthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        AddToCommand addTo = new AddToCommand(client, "add-to", "addToCommandExecution", "https://google.com");
        addTo.setShorten(true);

        assertEquals(
                "Please login to use this feature." + System.lineSeparator() +
                "If you don't have account use the register command"
                , addTo.execute(),
                "Should deny access for unauthorised users."
        );

    }

    @Test
    void addToShortenCommandAuthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "addToExec";
        String password = "R3@llY$tr0ng";

        Command register = new RegisterCommand("register", username, password);
        Command login = new LoginCommand(client, "login", username, password);
        AddToCommand addTo = new AddToCommand(client, "add-to", "addToShortenCommandAuthorised", "https://google.com");
        addTo.setShorten(true);

        register.execute();
        login.execute();

        assertEquals(
                "A group with name addToShortenCommandAuthorised does not exist."
                , addTo.execute(),
                "Should be able to access the add-to feature"
        );
    }
}