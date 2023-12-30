package code.src.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class ClientHandler implements Runnable {
    private Socket clSocket;
    private ExecutorService executor;
    private DataOutputStream dos;
    private DataInputStream dis;
    private Map<String, Set<String>> index;

    public ClientHandler(Socket clSocket, Map<String, Set<String>> index) throws IOException {
        this.clSocket = clSocket;
        this.index = index;
        dis = new DataInputStream(clSocket.getInputStream());
        dos = new DataOutputStream(clSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            boolean isConnected = true;
            String clientCommand;
            String serverCommands = """
                    Server options:
                    1. Sending a data (words separated by space) to the server;
                    2. Disconnecting from the server.
                    """;
            dos.writeUTF(serverCommands);
            while (isConnected) {
                clientCommand = dis.readUTF();
                switch (clientCommand) {
                    case "1" -> {
                        dos.writeUTF("Option 1. Send data (words separated by space).");
                        String clientResponse = dis.readUTF();
                        Set<String> result = index.get(clientResponse.split("\\W")[0]);
                        if (result.isEmpty()) {
                            dos.writeUTF("no file has such word");
                        }
                        dos.writeUTF("Result:\n" + result.toString());
                    }
                    case "2" -> {
                        dos.writeUTF("Connection stopped.\n");
                        isConnected = false;
                        if (executor != null) {
                            executor.shutdown();
                        }
                    }
                    default -> dos.writeUTF("Unknown operation.");
                }
            }
            try {
                if (dis != null) {
                    dis.close();
                }
                if (dos != null) {
                    dos.close();
                }
                if (clSocket != null) {
                    clSocket.close();
                }
                System.out.println("Client closed: " + clSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
