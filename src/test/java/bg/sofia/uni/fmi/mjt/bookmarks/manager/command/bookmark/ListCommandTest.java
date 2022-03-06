package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.LoginCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.RegisterCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListCommandTest {

    @Test
    void listUnauthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        Command list = new ListCommand(client);

        assertEquals(
                "Please login to use this feature." + System.lineSeparator() +
                "If you don't have account use the register command"
                , list.execute(),
                "Should deny access for unauthorised users."
        );
    }


    @Test
    void listAuthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "listExec";
        String password = "R3@llY$tr0ng";

        Command register = new RegisterCommand("register", username, password);
        Command login = new LoginCommand(client, "login", username, password);
        Command list = new ListCommand(client);

        register.execute();
        login.execute();

        assertEquals(
                "------------------------------\n"
                , list.execute(),
                "Should be able to access the list feature"
        );
    }
}