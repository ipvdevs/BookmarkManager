package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.AuthCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.misc.UnknownCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Status;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;

import java.nio.channels.SocketChannel;
import java.util.List;

public class SearchCommand extends AuthCommand {
    private static final int GROUP_NAME_TOKEN_ID = 2;

    private static final String INVALID_SEARCH_COMMAND = "Invalid usage of search command." + System.lineSeparator() +
                                                         "Type help to check the correct usage.";

    private boolean tagsFlag = false;
    private boolean titleFlag = false;

    public SearchCommand(SocketChannel sc, String... args) {
        super(sc, CommandType.SEARCH, args);
    }

    @Override
    public String execute() {
        Response<String> authResponse = Dispatcher.authManager().auth(sc);

        if (authResponse.status() == Status.ERROR) {
            return authResponse.response();
        }

        String caller = authResponse.response();

        if (tagsFlag) {
            List<String> tags = args.subList(2, args.size());

            return Dispatcher.bookmarkManager()
                    .searchByTags(tags, caller)
                    .response();
        }

        if (titleFlag) {
            return Dispatcher.bookmarkManager()
                    .searchByTitle(args.get(GROUP_NAME_TOKEN_ID), caller)
                    .response();
        }

        return new UnknownCommand(INVALID_SEARCH_COMMAND).execute();
    }

    public void tagsFlag(boolean tagsFlag) {
        this.tagsFlag = tagsFlag;
    }

    public void titleFlag(boolean titleFlag) {
        this.titleFlag = titleFlag;
    }
}
