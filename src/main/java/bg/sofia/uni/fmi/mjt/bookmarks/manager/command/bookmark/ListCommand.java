package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.AuthCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Status;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;

import java.nio.channels.SocketChannel;

public class ListCommand extends AuthCommand {
    private static final int GROUP_NAME_TOKEN_ID = 2;

    private boolean groupFlag = false;

    public ListCommand(SocketChannel sc, String... args) {
        super(sc, CommandType.LIST, args);
    }

    @Override
    public String execute() {
        Response<String> authResponse = Dispatcher.authManager().auth(sc);

        if (authResponse.status() == Status.ERROR) {
            return authResponse.response();
        }

        String caller = authResponse.response();

        Response<String> list = groupFlag ?
                Dispatcher.bookmarkManager().list(args.get(GROUP_NAME_TOKEN_ID), caller) :
                Dispatcher.bookmarkManager().list(caller);

        return list.response();
    }

    public void groupFlag(boolean groupFlag) {
        this.groupFlag = groupFlag;
    }
}
