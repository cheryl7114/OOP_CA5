package org.example.oop_ca5;

import org.example.oop_ca5.DAOs.MySqlCarDao;
import org.example.oop_ca5.DAOs.MySqlImageDao;
import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.DTOs.ImageMetadata;
import org.example.oop_ca5.Exceptions.DaoException;
import org.example.oop_ca5.Utilities.JSONConverter;
import org.json.JSONObject;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    final static int SERVER_PORT = 1024;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server is Starting on Port " + SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected");

                dataInputStream = new DataInputStream(clientSocket.getInputStream());
                dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                String command = dataInputStream.readUTF();

                if ("FIND_CAR_BY_ID".equals(command)) {
                    handleFindCarRequest();
                } else if ("GET_ALL_CARS".equals(command)) {
                    handleGetAllCarsRequest();
                } else if ("GET_IMAGES_LIST".equals(command)) {
                    handleGetImagesList(dataOutputStream);
                } else if ("GET_IMAGE".equals(command)) {
                    handleGetImage(dataInputStream, dataOutputStream);
                } else {
                    System.out.println("Invalid command!");
                }

                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleFindCarRequest() throws IOException {
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

    private void handleGetAllCarsRequest() throws IOException {
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

}