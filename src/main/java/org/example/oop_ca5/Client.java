package org.example.oop_ca5;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
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
            printMenu();
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();
                // exit client menu and notify server
                if (choice == 9) {
                    System.out.println("Exiting...");

                    try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                        dos.writeUTF("EXIT");
                        dos.flush();
                    } catch (IOException e) {
                        System.out.println("Couldn't notify server of exit: " + e.getMessage());
                    }
                    break;
                }
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
                        handleDownloadAllImages(dos, dis);
                    } else if (choice == 5) {
                        handleInsertCar(scanner, dos, dis);
                    } else if (choice == 6) {
                        handleDeleteCar(scanner, dos, dis);
                    } else if (choice == 7) {
                        handleGetAllRentals(dos, dis);
                    } else if (choice == 8) {
                        handleInsertRental(scanner, dos, dis);
                    } else {
                        System.out.println("Invalid choice! Enter a number 1-5");
                        scanner.nextLine();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next(); // Clear the invalid input
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n====== Car System Menu ======");
        System.out.println("1. Find Car by ID");
        System.out.println("2. Display All Cars");
        System.out.println("3. View available images");
        System.out.println("4. Download all images");
        System.out.println("5. Add a Car");
        System.out.println("6. Delete Car by ID");
        System.out.println("7. Display All Rentals");
        System.out.println("8. Add New Rental");
        System.out.println("9. Exit");
        System.out.println("=============================");
        System.out.print("Enter your choice: ");
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

    private void handleInsertCar(Scanner scanner, DataOutputStream dos, DataInputStream dis) throws IOException {
        try {
            System.out.println("\nEnter new car details:");

            System.out.print("Make: ");
            String make = scanner.nextLine().trim();

            System.out.print("Model: ");
            String model = scanner.nextLine().trim();

            System.out.print("Year: ");
            int year = scanner.nextInt();

            System.out.print("Rental Price Per Day: ");
            float rentalPrice = scanner.nextFloat();

            System.out.print("Available (true/false): ");
            boolean available = scanner.nextBoolean();

            JSONObject carJson = new JSONObject();
            carJson.put("make", make);
            carJson.put("model", model);
            carJson.put("year", year);
            carJson.put("rentalPricePerDay", rentalPrice);
            carJson.put("availability", available);

            dos.writeUTF("INSERT_CAR");
            dos.writeUTF(carJson.toString());

            String response = dis.readUTF();
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("error")) {
                System.out.println("Error: " + jsonResponse.getString("error"));
            } else {
                System.out.println("\nCar added successfully:");
                System.out.println("ID: " + jsonResponse.getInt("carID"));
                System.out.println("Make: " + jsonResponse.getString("make"));
                System.out.println("Model: " + jsonResponse.getString("model"));
                System.out.println("Year: " + jsonResponse.getInt("year"));
                System.out.printf("Price/Day: €%.2f%n", jsonResponse.getFloat("rentalPricePerDay"));
                System.out.println("Available: " + jsonResponse.getBoolean("availability"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleDeleteCar(Scanner scanner, DataOutputStream dos, DataInputStream dis) {
        try {
            System.out.print("\nEnter Car ID to delete: ");
            int carId = scanner.nextInt();

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("carID", carId);

            dos.writeUTF("DELETE_CAR");
            dos.writeUTF(jsonRequest.toString());

            String response = dis.readUTF();
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("error")) {
                System.out.println("Error: " + jsonResponse.getString("error"));
            } else {
                System.out.println("Success: " + jsonResponse.getString("message"));
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter a number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            scanner.nextLine();
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
                downloadImage(filename, false, false);
            }

        } catch (Exception e) {
            System.out.println("Error processing images list: " + e.getMessage());
        }
    }

    private boolean downloadImage(String filename, boolean skipExisting, boolean showDetailedProgress)
            throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            dos.writeUTF("GET_IMAGE");
            dos.writeUTF(filename);

            boolean success = dis.readBoolean();

            if (!success) {
                System.out.println("Error: " + dis.readUTF());
                return false;
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
                if (skipExisting) {
                    System.out.println("- File already exists, skipping");
                    return false;
                } else {
                    System.out.println("\nFile already exists: " + outputFile.getAbsolutePath());
                    System.out.println("Download cancelled.");
                    return false;
                }
            }

            // Download the file
            try (FileOutputStream fos = new FileOutputStream(outputFile);
                    BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;

                System.out.println(showDetailedProgress ? "- Downloading..." : "\nDownloading image...");

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
                        String progressLabel = showDetailedProgress ? "- Progress: " : "Progress: ";
                        System.out.print("\r" + progressLabel + progressPercentage + "%");
                    }
                }

                if (showDetailedProgress) {
                    System.out.println("\n- Download complete");
                } else {
                    System.out.println("\nDownload complete! Saved to " + outputFile.getAbsolutePath());
                }
                return true;
            }
        }
    }

    private void handleDownloadAllImages(DataOutputStream dos, DataInputStream dis) throws IOException {
        dos.writeUTF("GET_IMAGES_LIST");
        String jsonResponse = dis.readUTF();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);

            if (jsonArray.length() == 0) {
                System.out.println("No images available for download");
                return;
            }

            System.out.println("\nPreparing to download " + jsonArray.length() + " images...");

            // Create downloads directory
            File downloadsDir = new File("downloads");
            if (!downloadsDir.exists()) {
                downloadsDir.mkdir();
            }

            int successCount = 0;
            int skipCount = 0;
            int failCount = 0;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject img = jsonArray.getJSONObject(i);
                String filename = img.getString("filename");
                String imageName = img.getString("name");

                System.out.printf("\nProcessing %d/%d: %s (%s)\n",
                        i + 1, jsonArray.length(), imageName, filename);

                // Check if file already exists
                File outputFile = new File("downloads/" + filename);
                if (outputFile.exists()) {
                    System.out.println("- File already exists, skipping");
                    skipCount++;
                }

                try {
                    boolean downloadSuccess = downloadImage(filename, true, true); 
                    if (downloadSuccess) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    System.out.println("- Error downloading: " + e.getMessage());
                    failCount++;
                }
            }

            // Display summary
            System.out.println("\n===== Download Summary =====");
            System.out.println("Total images: " + jsonArray.length());
            System.out.println("Successfully downloaded: " + successCount);
            System.out.println("Skipped (already exists): " + skipCount);
            System.out.println("Failed: " + failCount);
            System.out.println("============================");
            System.out.println("All files saved to: " + downloadsDir.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error processing images list: " + e.getMessage());
        }
    }

    private void handleGetAllRentals(DataOutputStream dos, DataInputStream dis) throws IOException {
        dos.writeUTF("GET_ALL_RENTALS");
        String jsonResponse = dis.readUTF();

        try {
            JSONArray arr = new JSONArray(jsonResponse);
            System.out.println("\nAll Rentals:");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject r = arr.getJSONObject(i);
                System.out.printf(
                        "Rental ID: %d | Customer ID: %d | Car ID: %d | Start: %s | End: %s | Cost: €%.2f%n",
                        r.getInt("rentalID"),
                        r.getInt("customerID"),
                        r.getInt("carID"),
                        r.getString("startDate"),
                        r.getString("endDate"),
                        r.getDouble("totalCost")
                );
            }
        } catch (Exception e) {
            // If server returned an error JSON
            JSONObject err = new JSONObject(jsonResponse);
            System.out.println("Error: " + err.optString("error", e.getMessage()));
        }
    }

    private void handleInsertRental(Scanner scanner, DataOutputStream dos, DataInputStream dis) throws IOException {
        scanner.nextLine();
        System.out.println("\nEnter new rental details:");

        System.out.print("Customer ID: ");
        int customerID = scanner.nextInt();

        System.out.print("Car ID: ");
        int carID = scanner.nextInt();

        System.out.print("Start Date (YYYY-MM-DD): ");
        String startDate = scanner.next();

        System.out.print("End Date (YYYY-MM-DD): ");
        String endDate = scanner.next();

        System.out.print("Total Cost: ");
        float totalCost = scanner.nextFloat();

        // Build JSON request
        JSONObject rentalJson = new JSONObject();
        rentalJson.put("customerID", customerID);
        rentalJson.put("carID", carID);
        rentalJson.put("startDate", startDate);
        rentalJson.put("endDate", endDate);
        rentalJson.put("totalCost", totalCost);

        // Send to server
        dos.writeUTF("INSERT_RENTAL");
        dos.writeUTF(rentalJson.toString());

        // Read response
        String resp = dis.readUTF();
        JSONObject jr = new JSONObject(resp);
        if (jr.has("error")) {
            System.out.println("Error: " + jr.getString("error"));
        } else {
            System.out.println("\nRental added successfully:");
            // server returns the created rental as JSON
            System.out.printf(
                    "Rental ID: %d | Customer ID: %d | Car ID: %d | Start: %s | End: %s | Cost: €%.2f%n",
                    jr.getInt("rentalID"),
                    jr.getInt("customerID"),
                    jr.getInt("carID"),
                    jr.getString("startDate"),
                    jr.getString("endDate"),
                    jr.getDouble("totalCost")
            );
        }
    }
}