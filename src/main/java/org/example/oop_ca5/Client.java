package org.example.oop_ca5;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    final static int SERVER_PORT = 1024;
    final static String SERVER_HOST = "localhost";

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Find Car by ID");
            System.out.println("2. Display All Cars");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();

            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                if (choice == 1) {
                    handleFindCar(scanner, dos, dis);
                } else if (choice == 2) {
                    handleGetAllCars(dos, dis);
                } else if (choice == 3) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        scanner.close();
    }

    private void handleFindCar(Scanner scanner, DataOutputStream dos, DataInputStream dis) throws IOException {
        System.out.print("Enter Car ID: ");
        int carId = scanner.nextInt();

        dos.writeUTF("FIND_CAR_BY_ID");
        dos.writeInt(carId);

        if (dis.readBoolean()) {
            System.out.println("\nCar Details:");
            System.out.println("ID: " + dis.readInt());
            System.out.println("Make: " + dis.readUTF());
            System.out.println("Model: " + dis.readUTF());
            System.out.println("Year: " + dis.readInt());
            System.out.println("Price/Day: €" + dis.readFloat());
            System.out.println("Available: " + (dis.readBoolean() ? "Yes" : "No"));
        } else {
            System.out.println("Error: " + dis.readUTF());
        }
    }

    private void handleGetAllCars(DataOutputStream dos, DataInputStream dis) throws IOException {
        dos.writeUTF("GET_ALL_CARS");
        String jsonResponse = dis.readUTF();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            System.out.println("\nAll Cars:");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject car = jsonArray.getJSONObject(i);
                System.out.printf("ID: %d | Make: %-10s | Model: %-10s | Year: %d | Price: €%.2f | Available: %s%n",
                        car.getInt("carID"),
                        car.getString("make"),
                        car.getString("model"),
                        car.getInt("year"),
                        car.getFloat("rentalPricePerDay"),
                        car.getBoolean("availability") ? "Yes" : "No");
            }
        } catch (Exception e) {
            JSONObject error = new JSONObject(jsonResponse);
            System.out.println("Error: " + error.getString("error"));
        }
    }
}