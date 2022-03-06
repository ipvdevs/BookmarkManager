package bg.sofia.uni.fmi.mjt.bookmarks.manager.service;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Status;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.User;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.logger.Level;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.repository.Repository;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.Pair;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.pw.PasswordUtils;

import java.nio.channels.SocketChannel;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthManager implements AuthService {
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password.";

    private static final String INTERNAL_ERROR_MESSAGE = "An internal problem occurred. " +
                                                         "Please try again or contact an administrator";

    private final Repository<String, User> userRepository;
    private final Map<SocketChannel, User> logged;

    public AuthManager(Repository<String, User> userRepository) {
        this.userRepository = userRepository;
        this.logged = new HashMap<>();
    }

    @Override
    public Response<String> register(String username, String password) {
        Pair<Boolean, String> valid = validateRegisterCredentials(username, password);

        if (!valid.first()) {
            return new Response<>(Status.ERROR, valid.second());
        }

        String pwHash;
        try {
            pwHash = PasswordUtils.generateHash(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logGenerateHashErrors(e);

            return new Response<>(Status.ERROR, INTERNAL_ERROR_MESSAGE);
        }

        userRepository.add(username, new User(username, pwHash));

        return new Response<>(
                Status.OK,
                String.format("User %s successfully registered.", username)
        );
    }

    @Override
    public Response<String> login(SocketChannel session, String username, String password) {
        Pair<Boolean, String> valid = validateLoginCredentials(session, username, password);

        if (!valid.first()) {
            return new Response<>(Status.ERROR, valid.second());
        }

        if (isLoggedIn(session)) {
            return new Response<>(
                    Status.ERROR,
                    "You are already logged in."
            );
        }

        Optional<User> userOptional = userRepository.get(username);

        if (userOptional.isEmpty()) {
            return new Response<>(
                    Status.ERROR,
                    INVALID_CREDENTIALS_MESSAGE);
        }

        User target = userOptional.get();

        String[] pwHashTokens = target.getPwHash().split(":");

        String saltHex = pwHashTokens[0];
        String expectedHash = pwHashTokens[1];

        try {
            if (!PasswordUtils.verify(expectedHash, saltHex, password)) {
                return new Response<>(
                        Status.ERROR,
                        INVALID_CREDENTIALS_MESSAGE);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logGenerateHashErrors(e);

            return new Response<>(Status.ERROR, INTERNAL_ERROR_MESSAGE);
        }

        logged.put(session, target);

        return new Response<>(
                Status.OK,
                String.format("User %s logged in.", username));
    }

    private Pair<Boolean, String> validateLoginCredentials(SocketChannel session, String username, String password) {
        if (username == null) {
            String logMsg = AuthManager.class + " validateLoginCredentials(...): username is null.";
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);

            return new Pair<>(false, INTERNAL_ERROR_MESSAGE);
        }

        if (password == null) {
            String logMsg = AuthManager.class + " validateLoginCredentials(...): password is null.";
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);

            return new Pair<>(false, INTERNAL_ERROR_MESSAGE);
        }

        if (session == null) {
            String logMsg = AuthManager.class + " validateLoginCredentials(...): session is null.";
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);

            return new Pair<>(false, INTERNAL_ERROR_MESSAGE);
        }

        return new Pair<>(true, "VALID");
    }

    @Override
    public Response<String> auth(SocketChannel session) {
        if (session == null) {
            String logMsg = AuthManager.class + " auth(...): session is null.";
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);

            return new Response<>(Status.ERROR, INTERNAL_ERROR_MESSAGE);
        }

        if (!isLoggedIn(session)) {
            return new Response<>(
                    Status.ERROR,
                    "Please login to use this feature." + System.lineSeparator() +
                    "If you don't have account use the register command"
            );
        }

        return new Response<>(Status.OK, logged.get(session).getUsername());
    }

    @Override
    public Response<String> logout(SocketChannel session) {
        if (session == null) {
            String logMsg = AuthManager.class + " logout(...): session is null.";
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);

            return new Response<>(Status.ERROR, INTERNAL_ERROR_MESSAGE);
        }

        if (!isLoggedIn(session)) {
            return new Response<>(Status.ERROR, "You are not logged in.");
        }

        User user = logged.get(session);

        logged.remove(session);

        return new Response<>(
                Status.OK,
                String.format("User %s logged out.", user.getUsername())
        );
    }

    private boolean isLoggedIn(SocketChannel session) {
        return logged.containsKey(session);
    }

    private boolean isRegistered(String username) {
        return userRepository.contains(username);
    }

    private Pair<Boolean, String> validateRegisterCredentials(String username, String password) {
        if (username == null) {
            String logMsg = AuthManager.class + " validateRegisterCredentials(...): username is null.";
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);

            return new Pair<>(false, INTERNAL_ERROR_MESSAGE);
        }

        if (password == null) {
            String logMsg = AuthManager.class + " validateRegisterCredentials(...): password is null.";
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);

            return new Pair<>(false, INTERNAL_ERROR_MESSAGE);
        }

        if (username.equals(password)) {
            return new Pair<>(false, "Your password should not match the username. " +
                                     "Please, choose a different one.");
        }

        if (!PasswordUtils.validatePassword(password)) {
            return new Pair<>(false, "The password must contain at least one lowercase character, " +
                                     "one uppercase character, one digit, one special character, " +
                                     "and a length between 8 to 20.");
        }

        if (isRegistered(username)) {
            return new Pair<>(false, String.format("User %s already exists.", username));
        }

        return new Pair<>(true, "VALID");
    }

    private void logGenerateHashErrors(GeneralSecurityException e) {
        String logMsg = AuthManager.class + " " + e.getMessage();
        Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);
        Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));
    }
}
