package bg.sofia.uni.fmi.mjt.bookmarks.manager.service;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.Status;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.entity.User;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.repository.Repository;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.repository.UserStorage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthManagerTest {
    private static final String STRONG_PW = "R3@llY$tr0ng";

    Repository<String, User> registered = new UserStorage();
    AuthManager manager = new AuthManager(registered);

    @Test
    void registerWithInvalidPassword() {
        String message = "The password is not strong enough. User should not be registered.";

        String pwEmpty = "";
        String pw1 = "asd";
        String pw2 = "WEAK_PW";
        String pw3 = "testtest123";
        String pw4 = "Testtest123";
        String pw5 = "1234567890@#$";

        assertEquals(Status.ERROR, manager.register("pwEmpty", pwEmpty).status(), message);
        assertEquals(Status.ERROR, manager.register("username1", pw1).status(), message);
        assertEquals(Status.ERROR, manager.register("username2", pw2).status(), message);
        assertEquals(Status.ERROR, manager.register("username3", pw3).status(), message);
        assertEquals(Status.ERROR, manager.register("username4", pw4).status(), message);
        assertEquals(Status.ERROR, manager.register("username5", pw5).status(), message);
        assertTrue(registered.getAll().isEmpty());
    }

    @Test
    void registerWithUsernameMatchingPassword() {
        String message = "The password should not match the username. User should not be registered.";

        String username = "STRONG_pw123@";

        assertEquals(Status.ERROR, manager.register(username, username).status(), message);
        assertTrue(registered.getAll().isEmpty());
    }

    @Test
    void registerWithValidCredentials() {
        String message = "The credentials are valid. Registration should be successful.";

        String username = "HarryHacker";

        assertEquals(Status.OK, manager.register(username, STRONG_PW).status(), message);
        assertTrue(registered.contains(username));
    }

    @Test
    void registerWithAlreadyRegisteredUsername() {
        String username = "usernameExists";

        assertEquals(Status.OK, manager.register(username, STRONG_PW).status(),
                "The initial registration should be successful.");
        assertTrue(registered.contains(username));
        assertEquals(Status.ERROR, manager.register(username, STRONG_PW).status(),
                "User with this username already exists.");
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void passwordOfRegisterUserHashed() {
        String username = "hashed";

        assertEquals(Status.OK, manager.register(username, STRONG_PW).status(),
                "Registration should be successful. Password strong enough.");
        assertTrue(registered.contains(username),
                "The registered user should be added to the container.");

        assertEquals(username, registered.get(username).get().getUsername(),
                "The username should match the real username.");
        assertNotEquals(STRONG_PW, registered.get(username).get().getPwHash(),
                "The password should not be stored in plain text.");
    }

    @Test
    void loginUnregisteredUser() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "unregistered";

        assertEquals(Status.ERROR, manager.login(client, username, STRONG_PW).status(),
                "Such user is not registered. Login denied.");
    }

    @Test
    void loginRegisteredUser() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "valid_username";

        assertEquals(Status.OK, manager.register(username, STRONG_PW).status(),
                "Registration should be successful. Password strong enough.");
        assertEquals(Status.OK, manager.login(client, username, STRONG_PW).status(),
                "Login should be successful. Credentials are valid.");
    }

    @Test
    void loginRegisteredUserWithInvalidCredentials() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "credentials";

        String pwAttempt1 = "R3@llY$tr0n";
        String pwAttempt2 = "R3@llY$";
        String pwAttempt3 = "%%%%%%%%%%%%%%";
        String pwAttempt4 = "";

        assertEquals(Status.OK, manager.register(username, STRONG_PW).status(),
                "Registration should be successful. Password strong enough.");
        assertEquals(Status.ERROR, manager.login(client, username, pwAttempt1).status(),
                "Invalid credentials. Login denied.");
        assertEquals(Status.ERROR, manager.login(client, username, pwAttempt2).status(),
                "Invalid credentials. Login denied.");
        assertEquals(Status.ERROR, manager.login(client, username, pwAttempt3).status(),
                "Invalid credentials. Login denied.");
        assertEquals(Status.ERROR, manager.login(client, username, pwAttempt4).status(),
                "Invalid credentials. Login denied.");
        assertEquals(Status.ERROR, manager.login(client, username, username).status(),
                "Invalid credentials. Login denied.");
    }

    @Test
    void loginWhenAlreadyLoggedIn() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "loggedUsername";

        assertEquals(Status.OK, manager.register(username, STRONG_PW).status(),
                "Registration should be successful. Password strong enough.");
        assertEquals(Status.OK, manager.login(client, username, STRONG_PW).status(),
                "Login should be successful. Credentials are valid.");
        assertEquals(Status.ERROR, manager.login(client, username, STRONG_PW).status(),
                "User is already logged in. Login denied.");
    }

    @Test
    void logout() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "logoutUsername";

        assertEquals(Status.OK, manager.register(username, STRONG_PW).status(),
                "Registration should be successful. Password strong enough.");
        assertEquals(Status.OK, manager.login(client, username, STRONG_PW).status(),
                "Login should be successful. Credentials are valid.");
        assertEquals(Status.OK, manager.logout(client).status(),
                "User is logged in. Logout should be successful.");
    }

    @Test
    void logoutWhenNotLoggedIn() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        assertEquals(Status.ERROR, manager.logout(client).status(),
                "User is not logged in. Logout should be denied.");
    }

    @Test
    void authWhenLoggedIn() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "auth_user";

        assertEquals(Status.OK, manager.register(username, STRONG_PW).status(),
                "Registration should be successful. Password strong enough.");
        assertEquals(Status.OK, manager.login(client, username, STRONG_PW).status(),
                "Login should be successful. Credentials are valid.");
        assertEquals(Status.OK, manager.auth(client).status(),
                "User is logged in, authentication status should be OK.");
    }


    @Test
    void authWhenNotLoggedIn() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        assertEquals(Status.ERROR, manager.auth(client).status(),
                "User not logged in - auth access denied.");
    }


    @Test
    void authAfterLogout() {
        SocketChannel client = Mockito.mock(SocketChannel.class);

        String username = "auth_logout_user";

        assertEquals(Status.OK, manager.register(username, STRONG_PW).status(),
                "Registration should be successful. Password strong enough.");
        assertEquals(Status.OK, manager.login(client, username, STRONG_PW).status(),
                "Login should be successful. Credentials are valid.");
        assertEquals(Status.OK, manager.auth(client).status(),
                "User is logged in, authentication status should be OK.");
        assertEquals(Status.OK, manager.logout(client).status(),
                "User is logged in. Logout should be successful.");
        assertEquals(Status.ERROR, manager.auth(client).status(),
                "User not logged in - auth access denied.");
    }

    @Test
    void registerWithNullArgs() {
        assertEquals(Status.ERROR, manager.register(null, "password").status(),
                "Username is null. Error status expected.");
        assertEquals(Status.ERROR, manager.register("username", null).status(),
                "Password is null. Error status expected.");
    }

    @Test
    void loginWithNullArgs() {
        SocketChannel mockSc = Mockito.mock(SocketChannel.class);

        assertEquals(Status.ERROR, manager.login(null, "username", "password").status(),
                "Socketchannel is null. Error status expected.");
        assertEquals(Status.ERROR, manager.login(mockSc, null, "password").status(),
                "Username is null. Error status expected.");
        assertEquals(Status.ERROR, manager.login(mockSc, "username", null).status(),
                "Password is null. Error status expected.");
    }

    @Test
    void logoutWithNullArgs() {
        assertEquals(Status.ERROR, manager.logout(null).status(),
                "Socketchannel is null. Error status expected.");
    }

    @Test
    void authWithNullArgs() {
        assertEquals(Status.ERROR, manager.auth(null).status(),
                "Socketchannel is null. Error status expected.");
    }
}