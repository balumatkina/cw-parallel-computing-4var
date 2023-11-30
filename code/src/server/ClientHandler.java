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
            ArrayList<String> words = new ArrayList<>();

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
                        words.clear();
                        serverResponse(dos, "Option 1. Send data (words separated by space).");

                        for (String word : dis.readUTF().split(" ")) {
                            words.add(word);
                        }
                        String result = "";
                        for (String fileName : getResult(words)) {
                            result += fileName + "\n";
                        }
                        if (result.isEmpty()) {
                            result = "files not found";
                        }
                        serverResponse(dos, "Data successfully received. Result:\n" + result);
                    }
                    case "2" -> {
                        serverResponse(dos, "Option 2. Breaking connection.");

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

    private Set<String> getResult(ArrayList<String> words) {
        Set<String> result = index.get(words.get(0));
        if (result == null) {
            return Collections.emptySet();
        }
        words.remove(0);
        Set<String> temp;
        for (String word : words) {
            temp = index.get(word);
            if (temp != null) {
                result.retainAll(temp);
            } else {
                result.clear();
                break;
            }
        }
        return result;
    }

    private static void serverResponse(DataOutputStream dos, String message) throws IOException {
        dos.writeUTF(message);
        System.out.println(message);
    }
}
