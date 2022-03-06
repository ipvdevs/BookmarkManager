package bg.sofia.uni.fmi.mjt.bookmarks.manager.command;

import java.nio.channels.SocketChannel;

public abstract class AuthCommand extends Command {
    protected final SocketChannel sc;

    public AuthCommand(SocketChannel sc, CommandType type, String... args) {
        super(type, args);
        this.sc = sc;
    }

}
