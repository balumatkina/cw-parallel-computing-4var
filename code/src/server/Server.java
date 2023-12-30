package code.src.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private Map<String, Set<String>> index;
    private AtomicBoolean indexed;

    public Server(int portNumb, int indexingThreads) {
        index = new ConcurrentHashMap<>();
        indexed = new AtomicBoolean(false);
        try {
            (new Thread(new IndexingHandler(index, FileHandler.getFiles("texts"), indexingThreads, indexed))).start();

            ServerSocket serverSocket = new ServerSocket(portNumb);
            System.out.println("Server started: " + serverSocket);
            System.out.println("Waiting for client..\n");

            while (true) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket + "\n");
                    ClientHandler clientHandler = new ClientHandler(clientSocket, index, indexed);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                } catch (Exception e) {
                    clientSocket.close();
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
