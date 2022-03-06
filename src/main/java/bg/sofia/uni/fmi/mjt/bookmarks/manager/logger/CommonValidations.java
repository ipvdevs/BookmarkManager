package bg.sofia.uni.fmi.mjt.bookmarks.manager.logger;

public final class CommonValidations {

    private CommonValidations() {
    }

    public static void throwIfNull(Object o, String varName) {
        if (o == null) {
            throw new IllegalArgumentException(varName + " cannot be null!");
        }
    }

    public static void throwIfNullOrEmpty(String s, String varName) {
        throwIfNull(s, varName);

        if (s.isEmpty()) {
            throw new IllegalArgumentException(varName + " is empty!");
        }
    }
}
