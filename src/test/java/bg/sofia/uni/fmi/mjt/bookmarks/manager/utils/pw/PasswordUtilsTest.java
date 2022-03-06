package bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.pw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilsTest {

    @Test
    void generateHash() throws Exception {
        String password = "R3@llY$tr0ng";

        String hashed = PasswordUtils.generateHash(password);


        assertNotEquals(password, hashed, "The hashed string should not match the plain one.");
    }

    @Test
    void verifyHashValid() throws Exception {
        String message = "Valid hash data is passed to the verify method.";

        String pw1 = "R3@llY$tr0ng";
        String pw2 = "abcDEF123";
        String pw3 = "12345678901234567890";
        String pw4 = "12345678901234567890$";

        String hashed1 = PasswordUtils.generateHash(pw1);
        String hashed2 = PasswordUtils.generateHash(pw2);
        String hashed3 = PasswordUtils.generateHash(pw3);
        String hashed4 = PasswordUtils.generateHash(pw4);

        String[] tokens1 = hashed1.split(":");
        String[] tokens2 = hashed2.split(":");
        String[] tokens3 = hashed3.split(":");
        String[] tokens4 = hashed4.split(":");

        assertTrue(PasswordUtils.verify(tokens1[1], tokens1[0], pw1), message);
        assertTrue(PasswordUtils.verify(tokens2[1], tokens2[0], pw2), message);
        assertTrue(PasswordUtils.verify(tokens3[1], tokens3[0], pw3), message);
        assertTrue(PasswordUtils.verify(tokens4[1], tokens4[0], pw4), message);
    }

    @Test
    void verifyHashInvalid() throws Exception {
        String message = "Invalid hash data is passed to the verify method.";

        String pw = "R3@llY$tr0ng";

        String hashed = PasswordUtils.generateHash(pw);

        String[] tokens = hashed.split(":");

        assertFalse(PasswordUtils.verify("", tokens[0], pw), message);
        assertFalse(PasswordUtils.verify(tokens[1], "A1", pw), message);
        assertFalse(PasswordUtils.verify("", "A2", pw), message);
        assertFalse(PasswordUtils.verify(pw, "FFF", pw), message);
    }
}