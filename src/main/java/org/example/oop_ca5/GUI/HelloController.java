package org.example.oop_ca5.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class HelloController {

    @FXML
    protected void handleFindCarById() {
        showAlert("Find Car by ID", "This feature will be implemented soon.");
    }

    @FXML
    protected void handleDisplayAllCars() {
        showAlert("Display All Cars", "This feature will be implemented soon.");
    }

    @FXML
    protected void handleExit() {
        System.exit(0);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}