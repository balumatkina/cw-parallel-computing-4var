package code.src;

import java.util.Scanner;

import code.src.server.Server;

public class Main {
    public static void main(String[] args) {
        int threads = -1;
        Scanner in = new Scanner(System.in);
        do {
            try {
                System.out.print("Enter positive amount of threads for indexing: ");
                threads = in.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input, please try again.");
                continue;
            }
        } while (threads < 1);
        in.close();
        new Server(6060, threads);
    }
}