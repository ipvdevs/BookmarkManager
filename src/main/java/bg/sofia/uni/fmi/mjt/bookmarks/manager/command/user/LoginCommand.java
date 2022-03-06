package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;

import java.nio.channels.SocketChannel;

public class LoginCommand extends Command {
    private static final int USERNAME_TOKEN_ID = 1;
    private static final int PW_TOKEN_ID = 2;

    private final SocketChannel sc;

    public LoginCommand(SocketChannel sc, String... args) {
        super(CommandType.LOGIN, args);
        this.sc = sc;
    }

    @Override
    public String execute() {
        String username = args.get(USERNAME_TOKEN_ID);
        String password = args.get(PW_TOKEN_ID);

        return Dispatcher.authManager().login(sc, username, password).response();
    }

}
