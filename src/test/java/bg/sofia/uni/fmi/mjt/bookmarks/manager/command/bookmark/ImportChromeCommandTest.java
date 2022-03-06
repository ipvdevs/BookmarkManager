package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImportChromeCommandTest {

    @Test
    void importChromeUnauthorised() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        Command importChrome = new ImportChromeCommand(client);

        assertEquals(
                "Please login to use this feature." + System.lineSeparator() +
                "If you don't have account use the register command"
                , importChrome.execute(),
                "Should deny access for unauthorised users."
        );
    }

}