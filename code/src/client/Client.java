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
            String serverResponse;
            while (true) {
                serverResponse = dis.readUTF();
                System.out.println("SERVER: " + serverResponse);
                if (serverResponse.equals("Connection stopped.")) {
                    shutdown(scanner);
                    return;
                }
                System.out.print("CLIENT: ");
                String clientResponse = scanner.nextLine();
                dos.writeUTF(clientResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shutdown(Scanner scanner) {
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
    }

    public static void main(String[] args) {
        try {
            new Client("localhost", 6060);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}