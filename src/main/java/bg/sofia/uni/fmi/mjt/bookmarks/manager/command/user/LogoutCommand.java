package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.AuthCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;

import java.nio.channels.SocketChannel;

public class LogoutCommand extends AuthCommand {
    public LogoutCommand(SocketChannel sc, String... args) {
        super(sc, CommandType.LOGOUT, args);
    }

    @Override
    public String execute() {
        return Dispatcher.authManager().logout(sc).response();
    }
}
