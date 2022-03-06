package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.AuthCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Status;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;

import java.nio.channels.SocketChannel;

public class AddToCommand extends AuthCommand {
    private static final int GROUP_NAME_TOKEN_ID = 1;
    private static final int URL_TOKEN_ID = 2;

    private boolean shorten = false;

    public AddToCommand(SocketChannel sc, String... args) {
        super(sc, CommandType.ADD_TO, args);
    }

    public void setShorten(boolean shorten) {
        this.shorten = shorten;
    }

    @Override
    public String execute() {
        Response<String> authResponse = Dispatcher.authManager().auth(sc);

        if (authResponse.status() == Status.ERROR) {
            return authResponse.response();
        }

        String caller = authResponse.response();

        String groupName = args.get(GROUP_NAME_TOKEN_ID);
        String url = args.get(URL_TOKEN_ID);

        Response<String> addTo = shorten ?
                Dispatcher.bookmarkManager().addToShorten(groupName, url, caller) :
                Dispatcher.bookmarkManager().addTo(groupName, url, caller);

        return addTo.response();
    }
}
