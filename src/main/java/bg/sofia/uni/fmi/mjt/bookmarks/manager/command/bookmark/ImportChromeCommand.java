package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.AuthCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Status;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;

import java.nio.channels.SocketChannel;

public class ImportChromeCommand extends AuthCommand {
    public ImportChromeCommand(SocketChannel sc, String... args) {
        super(sc, CommandType.IMPORT_FROM_CHROME, args);
    }

    @Override
    public String execute() {
        Response<String> authResponse = Dispatcher.authManager().auth(sc);

        if (authResponse.status() == Status.ERROR) {
            return authResponse.response();
        }

        String caller = authResponse.response();

        return Dispatcher.bookmarkManager().importFromChrome(caller).response();
    }
}
