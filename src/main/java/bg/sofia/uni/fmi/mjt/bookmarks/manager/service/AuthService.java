package bg.sofia.uni.fmi.mjt.bookmarks.manager.service;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Response;

import java.nio.channels.SocketChannel;

/**
 * AuthService provides a client-server authentication service
 * to authenticate a user’s identity based on
 * username/password mechanism and
 * {@link SocketChannel} as a connection session controller.
 */
public interface AuthService {

    /**
     * Registration for a user by username and password.
     * It's performed a validation, uniqueness check, and password hashing.
     * If the security guarantees match the standards, the newly-registered user is stored.
     *
     * @param username unique username identifying the user.
     * @param password strong enough password.
     * @return Response with status and human-readable message ready to send to the client.
     */
    Response<String> register(String username, String password);

    /**
     * Login for user by username, password and {@link SocketChannel} used as client's session identifier.
     * Validations and existence checks are performed and if such user is valid, it is added.
     * to the login list.
     *
     * @param session  client's identifier.
     * @param username which is already registered.
     * @param password associated with this username.
     * @return Response with status and human-readable message ready to send to the client.
     */
    Response<String> login(SocketChannel session, String username, String password);

    /**
     * Checks if a {@link SocketChannel} client's session identifier figures in the login list.
     *
     * @param session client's identifier.
     * @return Response with status and human-readable message ready to send to the client.
     */
    Response<String> auth(SocketChannel session);

    /**
     * Checks if a {@link SocketChannel} client's session identifier figures
     * in the login list and if so - removes it from it.
     *
     * @param session client's identifier.
     * @return Response with status and human-readable message ready to send to the client.
     */
    Response<String> logout(SocketChannel session);

}
