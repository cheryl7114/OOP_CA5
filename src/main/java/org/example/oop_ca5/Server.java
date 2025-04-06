package org.example.oop_ca5;

import org.example.oop_ca5.DAOs.MySqlCarDao;
import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.Exceptions.DaoException;
import org.json.JSONArray;
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

            JSONArray jsonArray = new JSONArray();
            for (Car car : cars) {
                JSONObject jsonCar = new JSONObject();
                jsonCar.put("carID", car.getCarId());
                jsonCar.put("make", car.getMake());
                jsonCar.put("model", car.getModel());
                jsonCar.put("year", car.getYear());
                jsonCar.put("rentalPricePerDay", car.getRentalPricePerDay());
                jsonCar.put("availability", car.isAvailable());
                jsonArray.put(jsonCar);
            }
            dataOutputStream.writeUTF(jsonArray.toString());
        } catch (DaoException e) {
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            dataOutputStream.writeUTF(error.toString());
        }
    }
}