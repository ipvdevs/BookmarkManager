package bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.pw;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HexFormat;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordUtils {
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";

    private static final int HASH_ITERATIONS = 1000;
    private static final int KEY_LENGTH = 512;
    private static final int SALT_SIZE = 16;
    private static final int RADIX = 16;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtils() {

    }

    public static boolean validatePassword(String password) {
        Objects.requireNonNull(password, "Password cannot be null.");

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public static String generateHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = generateRandomSalt();

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = skf.generateSecret(spec).getEncoded();

        String saltHex = new BigInteger(1, salt).toString(RADIX);
        String hashHex = new BigInteger(1, hash).toString(RADIX);

        return String.format("%s:%s", saltHex, hashHex);
    }

    public static boolean verify(String expectedHash, String saltHex, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = saltFromHex(saltHex);

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = skf.generateSecret(spec).getEncoded();

        String actual = new BigInteger(1, hash).toString(RADIX);

        return actual.equals(expectedHash);
    }

    private static byte[] saltFromHex(String hex) {
        if (hex.length() % 2 == 1) {
            hex = "0" + hex;
        }

        return HexFormat.of().parseHex(hex);
    }

    private static byte[] generateRandomSalt() {
        byte[] salt = new byte[SALT_SIZE];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }
}
