package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.LoginCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user.RegisterCommand;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewGroupCommandTest {

    @Test
    void newGroupUnauthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        Command newGroup = new NewGroupCommand(client);

        assertEquals(
                "Please login to use this feature." + System.lineSeparator() +
                "If you don't have account use the register command"
                , newGroup.execute(),
                "Should deny access for unauthorised users."
        );
    }


    @Test
    void newGroupAuthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "newGroupExec";
        String password = "R3@llY$tr0ng";

        Command register = new RegisterCommand("register", username, password);
        Command login = new LoginCommand(client, "login", username, password);
        Command newGroup = new NewGroupCommand(client, "new-group", "name");

        register.execute();
        login.execute();

        assertEquals(
                "Group with name name is created."
                , newGroup.execute(),
                "Should be able to access the new-group feature"
        );
    }
}