package bg.sofia.uni.fmi.mjt.bookmarks.manager;

import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.Command;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.command.CommandParser;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.logger.Level;
import bg.sofia.uni.fmi.mjt.bookmarks.manager.service.Dispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server extends Thread {
    private static final String USER_STORAGE_PATH = "src/main/resources/users.json";
    private static final String BOOKMARKS_STORAGE_PATH = "src/main/resources/bookmarks.json";

    private final ServerConfig config;
    private Selector runningSelector;

    private static AtomicBoolean isRunning = new AtomicBoolean(false);

    public Server() {
        this(ServerConfig.newDefaultServerConfig());
    }

    public Server(Path configFile) {
        this(ServerConfig.newCustomServerConfig(configFile));
    }

    public Server(ServerConfig config) {
        this.config = config;
    }

    @Override
    public void run() {
        System.out.println("Starting the server...");

        ByteBuffer buffer = config.isBufferDirect() ?
                ByteBuffer.allocateDirect(config.bufferSize()) :
                ByteBuffer.allocate(config.bufferSize());

        isRunning.set(true);

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(config.hostname(), config.port()));
            serverSocketChannel.configureBlocking(config.isBlocking());

            System.out.println("Server started. Listening for connections...");

            Selector selector = Selector.open();
            this.runningSelector = selector;

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (isRunning.get()) {
                int readyChannels = selector.select();

                if (readyChannels <= 0 || !isRunning.get()) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIt = selectedKeys.iterator();

                while (keyIt.hasNext()) {
                    SelectionKey key = keyIt.next();

                    if (key.isAcceptable()) {
                        ServerSocketChannel sc = (ServerSocketChannel) key.channel();

                        SocketChannel accept = sc.accept();
                        accept.configureBlocking(config.isBlocking());
                        accept.register(selector, SelectionKey.OP_READ);

                        String address = accept.socket().getInetAddress().toString();
                        int port = accept.socket().getPort();

                        String connectMsg = String.format(
                                "Client with address %s:%d has opened a connection.",
                                address, port);

                        System.out.println(connectMsg);

                    } else if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();

                        buffer.clear();
                        int r = sc.read(buffer);

                        if (r < 0) {
                            String address = sc.socket().getInetAddress().toString();
                            int port = sc.socket().getPort();

                            String disconnectMsg = String.format(
                                    "Client with address %s:%d has closed the connection.",
                                    address, port);

                            System.out.println(disconnectMsg);
                            sc.close();
                            continue;
                        }

                        String line = new String(
                                buffer.array(),
                                0,
                                buffer.position(),
                                StandardCharsets.UTF_8);

                        Command command = CommandParser.of(line, sc);
                        String response = command.execute();

                        buffer.clear();
                        buffer.put(response.getBytes(StandardCharsets.UTF_8));
                        buffer.flip();

                        sc.write(buffer);
                    }

                    keyIt.remove();
                }

            }

        } catch (IOException e) {
            String logMsg = Server.class + " " + e.getMessage();
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), logMsg);
            Dispatcher.logger().log(Level.ERROR, LocalDateTime.now(), Arrays.toString(e.getStackTrace()));

            e.printStackTrace();
        }
    }

    public void stopServer() {
        if (!isRunning.get() || runningSelector == null) {
            System.err.println("Server is currently not running.");
        } else {
            System.out.println("Stopping the server...");
            isRunning.set(false);
            runningSelector.wakeup();
        }
    }

    public void storeData() {
        if (isRunning.get()) {
            System.out.println("Storing the data...");
            Dispatcher.bookmarkStorage().store(BOOKMARKS_STORAGE_PATH);
            Dispatcher.userStorage().store(USER_STORAGE_PATH);
        } else {
            System.err.println("Server is not running. Cannot store data!");
        }
    }

    public void loadData() {
        if (!isRunning.get()) {
            Dispatcher.bookmarkStorage().load(BOOKMARKS_STORAGE_PATH);
            Dispatcher.userStorage().load(USER_STORAGE_PATH);
        } else {
            System.err.println("Server is running. Cannot load data!");
        }
    }

}
