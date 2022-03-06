package bg.sofia.uni.fmi.mjt.bookmarks.manager.client;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.cli.CliPrompts;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static bg.sofia.uni.fmi.mjt.bookmarks.manager.utils.cli.CliPrompts.TOGGLE_COMMAND;

public class Client {
    private static final int SERVER_PORT = 62535;
    private static final String SERVER_HOSTNAME = "localhost";
    private static final int BUFFER_SIZE = 8192;

    private static final ByteBuffer BUFFER = ByteBuffer.allocate(BUFFER_SIZE);

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner in = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT));

            CliPrompts.initPrompt();

            while (true) {
                if (CliPrompts.promptToggled()) {
                    CliPrompts.prompt();
                }

                String userInput = in.nextLine();

                if (userInput.equalsIgnoreCase(TOGGLE_COMMAND)) {
                    CliPrompts.togglePrompt();
                    continue;
                }

                BUFFER.clear();
                BUFFER.put(userInput.getBytes(StandardCharsets.UTF_8));
                BUFFER.flip();

                socketChannel.write(BUFFER);

                BUFFER.clear();
                socketChannel.read(BUFFER);

                String response =
                        new String(
                                BUFFER.array(),
                                0,
                                BUFFER.position(),
                                StandardCharsets.UTF_8);

                System.out.println(response);
            }

        } catch (IOException e) {
            CliPrompts.communicationErrorMessage();
        }
    }
}
