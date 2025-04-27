package org.example.oop_ca5;

import org.example.oop_ca5.DAOs.MySqlCarDao;
import org.example.oop_ca5.DAOs.MySqlImageDao;
import org.example.oop_ca5.DAOs.MySqlRentalDao;
import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.DTOs.Rental;
import org.example.oop_ca5.DTOs.ImageMetadata;
import org.example.oop_ca5.Exceptions.DaoException;
import org.example.oop_ca5.Utilities.JSONConverter;
import org.json.JSONObject;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

public class Server {
    final static int SERVER_PORT = 1024;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server is Starting on Port " + SERVER_PORT);
    
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from: " + clientSocket.getInetAddress());
    
                // Create a new thread to handle this client
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void handleClient(Socket clientSocket) {
        try (
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            String command = dataInputStream.readUTF();

            if ("FIND_CAR_BY_ID".equals(command)) {
                handleFindCarRequest(dataInputStream, dataOutputStream);
            } else if ("GET_ALL_CARS".equals(command)) {
                handleGetAllCarsRequest(dataOutputStream);
            } else if ("INSERT_CAR".equals(command)) {
                handleInsertCarRequest(dataInputStream, dataOutputStream);
            } else if ("DELETE_CAR".equals(command)) {
                handleDeleteCarRequest(dataInputStream, dataOutputStream);
            } else if ("GET_IMAGES_LIST".equals(command)) {
                handleGetImagesList(dataOutputStream);
            } else if ("GET_IMAGE".equals(command)) {
                handleGetImage(dataInputStream, dataOutputStream);
            } else if ("GET_ALL_RENTALS".equals(command)) {
                handleGetAllRentals(dataOutputStream);
            } else if ("INSERT_RENTAL".equals(command)) {
                handleInsertRental(dataInputStream, dataOutputStream);
            }else if (command.equals("EXIT")) {
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
            } else {
                System.out.println("Invalid command: " + command);
            }
        } catch (Exception e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void handleFindCarRequest(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        try {
            int carId = dataInputStream.readInt();
            MySqlCarDao carDao = new MySqlCarDao();
            Car car = carDao.findCarById(carId);

            dataOutputStream.writeBoolean(true);
            dataOutputStream.writeInt(car.getCarId());
            dataOutputStream.writeUTF(car.getMake());
            dataOutputStream.writeUTF(car.getModel());
            dataOutputStream.writeInt(car.getYear());
            dataOutputStream.writeFloat(car.getRentalPricePerDay());
            dataOutputStream.writeBoolean(car.isAvailable());
        } catch (DaoException e) {
            dataOutputStream.writeBoolean(false);
            dataOutputStream.writeUTF("Error: " + e.getMessage());
        }
    }

    private void handleGetAllCarsRequest(DataOutputStream dataOutputStream) throws IOException {
        try {
            MySqlCarDao carDao = new MySqlCarDao();
            List<Car> cars = carDao.loadAllCars();

            String jsonString = JSONConverter.carListToJSONString(cars);

            dataOutputStream.writeUTF(jsonString);
        } catch (DaoException e) {
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            dataOutputStream.writeUTF(error.toString());
        }
    }

    private void handleInsertCarRequest(DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        try {
            String jsonCar = dataInputStream.readUTF();
            JSONObject json = new JSONObject(jsonCar);

            String make = json.getString("make");
            String model = json.getString("model");
            int year = json.getInt("year");
            float rentalPrice = (float) json.getDouble("rentalPricePerDay");
            boolean availability = json.getBoolean("availability");

            Car car = new Car(make, model, year, rentalPrice, availability);

            MySqlCarDao carDao = new MySqlCarDao();
            carDao.insertCar(car); // Updates car’s ID internally

            // Send success response
            String jsonResponse = JSONConverter.carObjectToJSONString(car);
            dataOutputStream.writeUTF(jsonResponse);

        } catch (DaoException e) {
            try {
                JSONObject error = new JSONObject();
                error.put("error", "DAO Error: " + e.getMessage());
                dataOutputStream.writeUTF(error.toString());
            } catch (IOException ex) {
                System.err.println("Failed to send error: " + ex.getMessage());
            }
        } catch (org.json.JSONException e) {
            try {
                JSONObject error = new JSONObject();
                error.put("error", "Invalid JSON: " + e.getMessage());
                dataOutputStream.writeUTF(error.toString());
            } catch (IOException ex) {
                System.err.println("Failed to send error: " + ex.getMessage());
            }
        } catch (IOException e) {
            try {
                JSONObject error = new JSONObject();
                error.put("error", "IO Error: " + e.getMessage());
                dataOutputStream.writeUTF(error.toString());
            } catch (IOException ex) {
                System.err.println("Failed to send error: " + ex.getMessage());
            }
        } catch (Exception e) {
            try {
                JSONObject error = new JSONObject();
                error.put("error", "Unexpected error: " + e.getMessage());
                dataOutputStream.writeUTF(error.toString());
            } catch (IOException ex) {
                System.err.println("Failed to send error: " + ex.getMessage());
            }
        }
    }

    private void handleDeleteCarRequest(DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        try {

            String jsonInput = dataInputStream.readUTF();
            JSONObject json = new JSONObject(jsonInput);
            int carId = json.getInt("carID");

            MySqlCarDao carDao = new MySqlCarDao();
            carDao.deleteCarById(carId); // Will throw DaoException if deletion fails

            JSONObject successJson = new JSONObject();
            successJson.put("status", "success");
            successJson.put("message", "Car with ID " + carId + " deleted successfully.");
            dataOutputStream.writeUTF(successJson.toString());

        } catch (DaoException e) {
            try {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", e.getMessage());
                dataOutputStream.writeUTF(error.toString());
            } catch (IOException ex) {
                System.err.println("Failed to send DAO error: " + ex.getMessage());
            }
        } catch (org.json.JSONException e) {
            try {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", "Invalid JSON: " + e.getMessage());
                dataOutputStream.writeUTF(error.toString());
            } catch (IOException ex) {
                System.err.println("Failed to send JSON error: " + ex.getMessage());
            }
        } catch (IOException e) {
            try {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", "IO Error: " + e.getMessage());
                dataOutputStream.writeUTF(error.toString());
            } catch (IOException ex) {
                System.err.println("Failed to send IO error: " + ex.getMessage());
            }
        }
    }

    private void handleGetImagesList(DataOutputStream dos) throws IOException {
        try {
            // Read image metadata from database
            MySqlImageDao imageDao = new MySqlImageDao();
            List<ImageMetadata> imagesList = imageDao.getAllImages();
            String jsonString = JSONConverter.imageListToJSONString(imagesList);
            // send json string to client
            dos.writeUTF(jsonString);
        } catch (Exception e) {
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", "Failed to get images list: " + e.getMessage());
            dos.writeUTF(errorJson.toString());
        }
    }

    // method to handle when client selects an image
    public void handleGetImage(DataInputStream dis, DataOutputStream dos) throws IOException {
        try {
            // read requested image filename by client
            String filename = dis.readUTF();

            // get image path file
            String imagePath = "src/main/resources/images/" + filename;
            File imageFile = new File(imagePath);

            if (!imageFile.exists()) {
                dos.writeBoolean(false);
                dos.writeUTF("Image file not found: " + filename);
                return;
            }

            // Send success indicator
            dos.writeBoolean(true);

            // Send file size
            long fileSize = imageFile.length();
            dos.writeLong(fileSize);

            // Send the file data
            try (FileInputStream fis = new FileInputStream(imageFile);
                    BufferedInputStream bis = new BufferedInputStream(fis)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                dos.flush();
            }
        } catch (Exception e) {
            dos.writeBoolean(false);
            dos.writeUTF("Error sending image: " + e.getMessage());
        }
    }

    private void handleGetAllRentals(DataOutputStream dos) throws IOException {
        try {
            MySqlRentalDao rentalDao = new MySqlRentalDao();
            List<Rental> rentals = rentalDao.loadAllRentals();
            String jsonString = JSONConverter.rentalListToJSONString(rentals);
            dos.writeUTF(jsonString);
        } catch (DaoException e) {
            JSONObject err = new JSONObject();
            err.put("error", e.getMessage());
            dos.writeUTF(err.toString());
        }
    }

    private void handleInsertRental(DataInputStream dis, DataOutputStream dos) {
        try {
            String jsonReq = dis.readUTF();
            JSONObject j = new JSONObject(jsonReq);

            int customerID = j.getInt("customerID");
            int carID = j.getInt("carID");
            LocalDate start = LocalDate.parse(j.getString("startDate"));
            LocalDate end = LocalDate.parse(j.getString("endDate"));
            float cost = (float) j.getDouble("totalCost");

            // insert rental
            Rental rental = new Rental(customerID, carID, start, end, cost);
            MySqlRentalDao rentalDao = new MySqlRentalDao();
            rentalDao.insertRental(rental);

            // now mark the car unavailable
            MySqlCarDao carDao = new MySqlCarDao();
            Car car = carDao.findCarById(carID);
            car.setAvailability(false);
            carDao.updateCar(car);

            // send back the rental
            String resp = JSONConverter.rentalObjectToJSONString(rental);
            dos.writeUTF(resp);

        } catch (DaoException e) {
            JSONObject err = new JSONObject();
            err.put("error", "DAO Error: " + e.getMessage());

        } catch (Exception e) {
            JSONObject err = new JSONObject();
            err.put("error", "Error: " + e.getMessage());
        }
    }}