package org.example.oop_ca5;

import org.example.oop_ca5.DAOs.MySqlCarDao;
import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.Exceptions.DaoException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    final static int SERVER_PORT = 1024;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server is Starting on Port " + SERVER_PORT);

            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected");

            // Pass clientSocket to receiveCarRequest()
            receiveCarRequest(clientSocket);

            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveCarRequest(Socket clientSocket) throws Exception {
        try (DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream())) {
            int carId = dataInputStream.readInt();
            System.out.println("Server: Received request for Car ID " + carId);

            MySqlCarDao carDao = new MySqlCarDao();
            try {
                Car car = carDao.findCarById(carId);
                sendCarResponse(clientSocket, car);
            } catch (DaoException e) {
                sendErrorResponse(clientSocket, "Car not found with ID: " + carId);
            }
        }
    }

    private void sendCarResponse(Socket socket, Car car) throws IOException {
        try (DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            dataOutputStream.writeBoolean(true);
            dataOutputStream.writeInt(car.getCarId());
            dataOutputStream.writeUTF(car.getMake());
            dataOutputStream.writeUTF(car.getModel());
            dataOutputStream.writeInt(car.getYear());
            dataOutputStream.writeFloat(car.getRentalPricePerDay());
            dataOutputStream.writeBoolean(car.isAvailable());
            dataOutputStream.flush();
        }
    }

    private void sendErrorResponse(Socket socket, String message) throws IOException {
        try (DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            dataOutputStream.writeBoolean(false);
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
        }
    }
}