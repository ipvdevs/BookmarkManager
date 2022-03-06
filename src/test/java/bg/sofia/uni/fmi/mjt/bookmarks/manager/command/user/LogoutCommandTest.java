package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogoutCommandTest {
    @Test
    void logoutWithCommandExecution() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "logoutExec";
        String password = "R3@llY$tr0ng";

        Command register = new RegisterCommand("register", username, password);
        Command login = new LoginCommand(client, "login", username, password);
        Command logout = new LogoutCommand(client);

        assertEquals("User logoutExec successfully registered.", register.execute(),
                "Registration should be successful. Password strong enough.");
        assertEquals("User logoutExec logged in.", login.execute(),
                "Login should be successful. User exists and credentials are valid.");
        assertEquals("User logoutExec logged out.", logout.execute(),
                "Logout should be successful. User exists and credentials are valid.");
    }
}