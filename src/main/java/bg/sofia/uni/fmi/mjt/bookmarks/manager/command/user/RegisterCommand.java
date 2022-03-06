package bg.sofia.uni.fmi.mjt.bookmarks.manager.command.user;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;

public class RegisterCommand extends Command {
    private static final int USERNAME_TOKEN_ID = 1;
    private static final int PW_TOKEN_ID = 2;

    public RegisterCommand(String... args) {
        super(CommandType.REGISTER, args);
    }

    @Override
    public String execute() {
        String username = args.get(USERNAME_TOKEN_ID);
        String password = args.get(PW_TOKEN_ID);

        return Dispatcher.authManager().register(username, password).response();
    }
}
