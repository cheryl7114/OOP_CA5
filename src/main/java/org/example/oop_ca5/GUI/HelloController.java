package org.example.oop_ca5.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class HelloController {
    // Constants for server connection
    final static int SERVER_PORT = 1024;
    final static String SERVER_HOST = "localhost";

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

                // Display car details in a dialog
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Car Details");
                alert.setHeaderText("Car Found");

                // Create content for the dialog
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                grid.add(new Label("ID:"), 0, 0);
                grid.add(new Label(String.valueOf(id)), 1, 0);

                grid.add(new Label("Make:"), 0, 1);
                grid.add(new Label(make), 1, 1);

                grid.add(new Label("Model:"), 0, 2);
                grid.add(new Label(model), 1, 2);

                grid.add(new Label("Year:"), 0, 3);
                grid.add(new Label(String.valueOf(year)), 1, 3);

                grid.add(new Label("Price/Day:"), 0, 4);
                grid.add(new Label(String.format("€%.2f", price)), 1, 4);

                grid.add(new Label("Available:"), 0, 5);
                grid.add(new Label(available ? "Yes" : "No"), 1, 5);

                alert.getDialogPane().setContent(grid);
                alert.showAndWait();
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
        // For now, just show a notification that this feature isn't implemented yet
        showAlert("Display All Cars", "This feature will be implemented soon.", Alert.AlertType.INFORMATION);
    }

    @FXML
    protected void handleExit() {
        System.exit(0);
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}