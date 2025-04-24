package org.example.oop_ca5.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.oop_ca5.DTOs.Car;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class HelloController {
    // Constants for server connection
    final static int SERVER_PORT = 1024;
    final static String SERVER_HOST = "localhost";

    // Main panes
    @FXML
    private VBox mainMenuPane;

    @FXML
    private VBox carDetailsPane;

    @FXML
    private VBox allCarsPane;

    // Car details labels
    @FXML
    private Label idLabel;

    @FXML
    private Label makeLabel;

    @FXML
    private Label modelLabel;

    @FXML
    private Label yearLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label availableLabel;

    // Table for all cars
    @FXML
    private TableView<Car> carsTableView;

    @FXML
    private TableColumn<Car, Integer> idColumn;

    @FXML
    private TableColumn<Car, String> makeColumn;

    @FXML
    private TableColumn<Car, String> modelColumn;

    @FXML
    private TableColumn<Car, Integer> yearColumn;

    @FXML
    private TableColumn<Car, Float> priceColumn;

    @FXML
    private TableColumn<Car, Boolean> availableColumn;

    @FXML
    public void initialize() {
        // Initialize TableView columns using the DTO Car class
        idColumn.setCellValueFactory(new PropertyValueFactory<>("carId"));
        makeColumn.setCellValueFactory(new PropertyValueFactory<>("make"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("rentalPricePerDay"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        // Formatter for price column to show currency
        priceColumn.setCellFactory(column -> new TableCell<Car, Float>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("€%.2f", item));
                }
            }
        });

        // Formatter for available column to show Yes/No
        availableColumn.setCellFactory(column -> new TableCell<Car, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Yes" : "No");
                }
            }
        });
    }

    @FXML
    protected void handleFindCarById() {
        // Create a dialog for entering car ID
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Find Car by ID");
        dialog.setHeaderText("Enter Car ID");
        dialog.setContentText("ID:");

        // Get the input result
        Optional<String> result = dialog.showAndWait();

        // Process the result if present
        if (result.isPresent()) {
            String carIdStr = result.get();
            try {
                int carId = Integer.parseInt(carIdStr);
                findCarById(carId);
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number for Car ID.", Alert.AlertType.ERROR);
            }
        }
    }

    private void findCarById(int carId) {
        try {
            // Open connection to server
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // Send command to server
            dos.writeUTF("FIND_CAR_BY_ID");
            dos.writeInt(carId);

            // Process response
            boolean success = dis.readBoolean();
            if (success) {
                // Car found - read details
                int id = dis.readInt();
                String make = dis.readUTF();
                String model = dis.readUTF();
                int year = dis.readInt();
                float price = dis.readFloat();
                boolean available = dis.readBoolean();

                // Close the connection
                socket.close();

                // Update UI with car details
                idLabel.setText(String.valueOf(id));
                makeLabel.setText(make);
                modelLabel.setText(model);
                yearLabel.setText(String.valueOf(year));
                priceLabel.setText(String.format("€%.2f", price));
                availableLabel.setText(available ? "Yes" : "No");

                // Show car details pane and hide main menu
                showPane(carDetailsPane);
            } else {
                // Car not found or error occurred
                String errorMsg = dis.readUTF();
                socket.close();
                showAlert("Error", errorMsg, Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Connection Error", "Failed to connect to server: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleDisplayAllCars() {
        try {
            // Open connection to server
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // Send command to server
            dos.writeUTF("GET_ALL_CARS");

            // Read JSON response from server
            String jsonResponse = dis.readUTF();
            socket.close();

            // Parse JSON response
            JSONArray jsonArray = new JSONArray(jsonResponse);
            ObservableList<Car> carsList = FXCollections.observableArrayList();

            // Convert JSON objects to Car objects
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonCar = jsonArray.getJSONObject(i);
                Car car = new Car(
                        jsonCar.getInt("carID"),
                        jsonCar.getString("make"),
                        jsonCar.getString("model"),
                        jsonCar.getInt("year"),
                        (float) jsonCar.getDouble("rentalPricePerDay"),
                        jsonCar.getBoolean("availability")
                );
                carsList.add(car);
            }

            // Update the TableView
            carsTableView.setItems(carsList);

            // Show the all cars pane
            showPane(allCarsPane);

        } catch (IOException e) {
            showAlert("Connection Error", "Failed to connect to server: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "Failed to process car data: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleBackToMenu() {
        // Hide all content panes and show main menu
        showPane(mainMenuPane);
    }

    @FXML
    protected void handleExit() {
        System.exit(0);
    }

    private void showPane(VBox paneToShow) {
        // Hide all panes
        mainMenuPane.setVisible(false);
        carDetailsPane.setVisible(false);
        allCarsPane.setVisible(false);

        // Show requested pane
        paneToShow.setVisible(true);
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}