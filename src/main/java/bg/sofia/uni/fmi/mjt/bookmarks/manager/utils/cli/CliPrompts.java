package bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.cli;

public class CliPrompts {
    public static final String TOGGLE_COMMAND = "toggle-prompt";

    private static final int HEADER_SIZE = 30;

    private static boolean prompt = false;

    private CliPrompts() {
    }

    public static void togglePrompt() {
        prompt = !prompt;
    }

    public static boolean promptToggled() {
        return prompt;
    }

    public static void prompt() {
        System.out.print("> ");
    }

    public static void communicationErrorMessage() {
        System.out.println("A problem occurred with the network communication.");
    }

    public static void initPrompt() {
        System.out.println("Welcome!");
        System.out.println("Type help to list the available commands.");
        System.out.println("Type toggle-prompt to toggle the CLI prompt.");
    }

    public static String header() {
        return "-".repeat(HEADER_SIZE) + System.lineSeparator();
    }
}
