package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.LoginCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.RegisterCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoveFromCommandTest {
    @Test
    void removeFromUnauthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        Command removeFrom = new RemoveFromCommand(client);

        assertEquals(
                "Please login to use this feature." + System.lineSeparator() +
                "If you don't have account use the register command"
                , removeFrom.execute(),
                "Should deny access for unauthorised users."
        );
    }


    @Test
    void removeFromAuthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "removeFromExec";
        String password = "R3@llY$tr0ng";

        Command register = new RegisterCommand("register", username, password);
        Command login = new LoginCommand(client, "login", username, password);
        Command removeFrom = new RemoveFromCommand(client, "remove-form", "group", "link");

        register.execute();
        login.execute();

        assertEquals(
                "A bookmark group with name group does not exist."
                , removeFrom.execute(),
                "Should be able to access the remove-from feature"
        );
    }
}