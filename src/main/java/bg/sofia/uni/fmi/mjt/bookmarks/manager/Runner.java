package bg.sofia.uni.fmi.mjt.bookmarks.manager;

import java.util.Scanner;

public class Runner {

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();

        Scanner in = new Scanner(System.in);

        String line;

        while (true) {
            line = in.nextLine();

            if (line.equals("start")) {
                server.loadData();
                server.start();
            }

            if (line.equals("store")) {
                server.storeData();
            }

            if (line.equals("stop")) {
                server.storeData();
                server.stopServer();
                server.join();
                break;
            }

        }

    }

}
