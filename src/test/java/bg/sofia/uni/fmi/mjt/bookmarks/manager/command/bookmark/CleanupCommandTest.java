package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.LoginCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.RegisterCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CleanupCommandTest {

    @Test
    void cleanupUnauthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        Command cleanup = new CleanupCommand(client);

        assertEquals(
                "Please login to use this feature." + System.lineSeparator() +
                "If you don't have account use the register command"
                , cleanup.execute(),
                "Should deny access for unauthorised users."
        );
    }


    @Test
    void cleanupAuthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "cleanupExec";
        String password = "R3@llY$tr0ng";

        Command register = new RegisterCommand("register", username, password);
        Command login = new LoginCommand(client, "login", username, password);
        Command cleanup = new CleanupCommand(client);

        register.execute();
        login.execute();

        assertEquals(
                "Cleanup completed. Totally removed invalid bookmarks: 0"
                , cleanup.execute(),
                "Should be able to access the cleanup feature"
        );
    }
}