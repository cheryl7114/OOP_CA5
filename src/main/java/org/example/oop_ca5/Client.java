package org.example.oop_ca5;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    final int SERVER_PORT = 1024;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        try (Socket socket = new Socket("localhost", SERVER_PORT)) {
            // Get input from user
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Car ID: ");
            int carId = scanner.nextInt();
            scanner.close();

            // Set up data streams
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());

            // Send Car ID to server
            dataOut.writeInt(carId);
            dataOut.flush();

            // Receive response from server
            boolean success = dataIn.readBoolean();
            if (success) {
                System.out.println("\nCar Details:");
                System.out.println("ID: " + dataIn.readInt());
                System.out.println("Make: " + dataIn.readUTF());
                System.out.println("Model: " + dataIn.readUTF());
                System.out.println("Year: " + dataIn.readInt());
                System.out.println("Price/Day: €" + dataIn.readFloat());
                System.out.println("Available: " + (dataIn.readBoolean() ? "Yes" : "No"));
            } else {
                System.out.println("Error: " + dataIn.readUTF());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

