package code.src.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;

    public Client(String address, int port) {
        Scanner scanner = new Scanner(System.in);
        try {
            socket = new Socket(address, port);
            System.out.println("The client is connected\n" + socket + "\n");

            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException u) {
            u.printStackTrace();
        }

        try {
            serverRead(dis);

            while (true) {
                System.out.println("Enter command number (1-5): ");
                String command = scanner.nextLine();

                dos.writeUTF(command);

                switch (command) {
                    case "1" -> {

                        serverRead(dis);

                        dos.writeUTF("test file names");

                        serverRead(dis);
                    }
                    case "2" ->
                        serverRead(dis);
                    case "3" -> {
                        serverRead(dis);
                        serverRead(dis);
                    }
                    case "4" -> {
                        serverRead(dis);
                        serverRead(dis);

                        try {
                            if (dis != null) {
                                dis.close();
                            }
                            if (dos != null) {
                                dos.close();
                            }
                            if (socket != null) {
                                socket.close();
                            }
                            if (scanner != null) {
                                scanner.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return;
                    }
                    default -> System.out.println("Invalid operation number. Please try again.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new Client("localhost", 6060);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void serverRead(DataInputStream dis) throws IOException {
        String serverResponse = dis.readUTF();
        System.out.println("SERVER: " + serverResponse);
    }

}