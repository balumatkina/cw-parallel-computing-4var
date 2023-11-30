package code.src.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public Server(int portNumb) {
        try {
            ServerSocket serverSocket = new ServerSocket(portNumb);
            System.out.println("Server started: " + serverSocket);
            System.out.println("Waiting for client..\n");

            while (true) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket + "\n");

                    // Multi client handle
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
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
