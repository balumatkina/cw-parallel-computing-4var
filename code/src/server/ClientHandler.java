package code.src.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ClientHandler implements Runnable {
    private Socket clSocket;
    private ExecutorService executor;
    private DataOutputStream dos;
    private DataInputStream dis;

    public ClientHandler(Socket clSocket) throws IOException {
        this.clSocket = clSocket;
        dis = new DataInputStream(clSocket.getInputStream());
        dos = new DataOutputStream(clSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            CompletableFuture<String> future = new CompletableFuture<>();
            boolean isConnected = true;
            String clientCommand;
            ArrayList<String> array = new ArrayList<>();

            String serverCommands = """
                    Server options:
                    1. Sending a data to the server;
                    2. Starting processing;
                    3. Getting results;
                    4. Disconnecting from the server.
                    """;
            dos.writeUTF(serverCommands);

            while (isConnected) {
                clientCommand = dis.readUTF();
                switch (clientCommand) {
                    case "1" -> {
                        array.clear();
                        serverResponse(dos, "Option 1. Getting data.");

                        array.add(dis.readUTF());

                        serverResponse(dos, "Data successfully received.\n");
                    }
                    case "2" -> {
                        serverResponse(dos, "Option 2. Starting processing.\n");

                        // Future starting
                        future = CompletableFuture.supplyAsync(() -> "Test data processed: " + array.getFirst());
                    }
                    case "3" -> {
                        serverResponse(dos, "Option 3. Getting results.");

                        if (future != null && future.isDone()) {
                            String result = future.getNow(null);
                            serverResponse(dos, "Result: " + result);
                            future = null;
                        } else {
                            serverResponse(dos, "At the moment results are not ready.\n");
                        }
                    }
                    case "4" -> {
                        serverResponse(dos, "Option 4. Breaking connection.");

                        isConnected = false;
                        if (executor != null) {
                            executor.shutdown();
                        }

                        serverResponse(dos, "Connection stopped.\n");
                    }
                    default -> System.err.println("Unknown operation.");
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

    private static void serverResponse(DataOutputStream dos, String message) throws IOException {
        dos.writeUTF(message);
        System.out.println(message);
    }
}
