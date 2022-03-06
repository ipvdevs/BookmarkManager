package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.AuthCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Status;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;

import java.nio.channels.SocketChannel;

public class NewGroupCommand extends AuthCommand {
    private static final int GROUP_NAME_TOKEN_ID = 1;

    public NewGroupCommand(SocketChannel sc, String... args) {
        super(sc, CommandType.NEW_GROUP, args);
    }

    @Override
    public String execute() {
        Response<String> authResponse = Dispatcher.authManager().auth(sc);

        if (authResponse.status() == Status.ERROR) {
            return authResponse.response();
        }

        String caller = authResponse.response();
        String groupName = args.get(GROUP_NAME_TOKEN_ID);

        return Dispatcher.bookmarkManager().createGroup(groupName, caller).response();
    }
}
