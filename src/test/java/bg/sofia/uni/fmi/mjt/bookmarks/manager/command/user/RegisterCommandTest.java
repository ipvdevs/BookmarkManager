package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegisterCommandTest {

    @Test
    void registerCommandExecution() {
        String username = "registerExec";
        String password = "R3@llY$tr0ng";

        Command register = new RegisterCommand("register", username, password);

        assertEquals("User registerExec successfully registered.", register.execute(),
                "Registration should be successful. Password strong enough.");
    }

}