package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginCommandTest {

    @Test
    void loginWithCommandExecution() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "loginExec";
        String password = "R3@llY$tr0ng";

        Command register = new RegisterCommand("register", username, password);
        Command login = new LoginCommand(client, "login", username, password);

        assertEquals("User loginExec successfully registered.", register.execute(),
                "Registration should be successful. Password strong enough.");
        assertEquals("User loginExec logged in.", login.execute(),
                "Login should be successful.  Logout should be successful.");
    }

}