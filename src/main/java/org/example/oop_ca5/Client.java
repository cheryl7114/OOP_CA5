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
            System.out.println("3. View available images");
            System.out.println("4. Exit");
            int choice = scanner.nextInt();

            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                if (choice == 1) {
                    handleFindCar(scanner, dos, dis);
                } else if (choice == 2) {
                    handleGetAllCars(dos, dis);
                } else if (choice == 3) {
                    handleGetImagesList(dos, dis, scanner);
                } else if (choice == 4) {
                    break;
                } else {
                    System.out.println("Invalid choice! Enter a number 1-4");
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

    private void handleGetImagesList(DataOutputStream dos, DataInputStream dis, Scanner scanner) throws IOException {
        dos.writeUTF("GET_IMAGES_LIST");

        String jsonResponse = dis.readUTF();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);

            if (jsonArray.length() == 0) {
                System.out.println("No images available");
                return;
            }

            System.out.println("\nAvailable Images: ");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject img = jsonArray.getJSONObject(i);
                System.out.printf("%d. %s (Car ID: %d)%n",
                        i + 1,
                        img.getString("name"),
                        img.getInt("carID"));
            }

            System.out.print("\nEnter image number to download (0 to cancel): ");
            int choice = scanner.nextInt();

            if (choice > 0 && choice <= jsonArray.length()) {
                String filename = jsonArray.getJSONObject(choice - 1).getString("filename");
                downloadImage(filename, scanner);
            }
            
        } catch (Exception e) {
            System.out.println("Error processing images list: " + e.getMessage());
        }
    }

    private void downloadImage(String filename, Scanner scanner) throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            dos.writeUTF("GET_IMAGE");
            dos.writeUTF(filename);

            boolean success = dis.readBoolean();

            if (!success) {
                System.out.println("Error: " + dis.readUTF());
                return;
            }

            // Read file size
            long fileSize = dis.readLong();

            // Create directory if it doesn't exist
            File downloadsDir = new File("downloads");
            if (!downloadsDir.exists()) {
                downloadsDir.mkdir();
            }

            // Create the output file
            File outputFile = new File("downloads/" + filename);

            // Check if the file already exists
            if (outputFile.exists()) {
                System.out.println("\nFile already exists: " + outputFile.getAbsolutePath());
                System.out.println("Download cancelled.");
                return;
            }

            // Download the file
            try (FileOutputStream fos = new FileOutputStream(outputFile);
                    BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;

                System.out.println("\nDownloading image...");

                // Simple progress indicator
                int progressPercentage = 0;
                while (totalBytesRead < fileSize) {
                    bytesRead = dis.read(buffer, 0, buffer.length);
                    if (bytesRead == -1)
                        break;

                    bos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    // Update progress
                    int newProgressPercentage = (int) ((totalBytesRead * 100) / fileSize);
                    if (newProgressPercentage > progressPercentage) {
                        progressPercentage = newProgressPercentage;
                        System.out.print("\rProgress: " + progressPercentage + "%");
                    }
                }

                System.out.println("\nDownload complete! Saved to " + outputFile.getAbsolutePath());
            }
        }
    }
}