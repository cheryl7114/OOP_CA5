package org.example.oop_ca5.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.oop_ca5.DAOs.MySqlCarDao;
import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.DTOs.Rental;
import org.example.oop_ca5.Exceptions.DaoException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HelloController {
    // Constants for server connection
    final static int SERVER_PORT = 1024;
    final static String SERVER_HOST = "localhost";

    // Main panes
    @FXML private VBox mainMenuPane;
    @FXML private VBox carDetailsPane;
    @FXML private VBox allCarsPane;
    @FXML private VBox allRentalsPane;
    @FXML private VBox insertCarPane;
    @FXML private VBox deleteCarPane;
    @FXML private VBox insertRentalPane;

    // Car details labels
    @FXML private Label idLabel, makeLabel, modelLabel, yearLabel, priceLabel, availableLabel;

    // Table for all cars
    @FXML private TableView<Car> carsTableView;
    @FXML private TableColumn<Car,Integer> idColumn;
    @FXML private TableColumn<Car,String> makeColumn;
    @FXML private TableColumn<Car,String> modelColumn;
    @FXML private TableColumn<Car,Integer> yearColumn;
    @FXML private TableColumn<Car,Float> priceColumn;
    @FXML private TableColumn<Car,Boolean> availableColumn;

    // Table for all rentals
    @FXML private TableView<Rental> rentalsTableView;
    @FXML private TableColumn<Rental,Integer> rentalIdColumn;
    @FXML private TableColumn<Rental,Integer> customerIdColumn;
    @FXML private TableColumn<Rental,Integer> carIdRentalColumn;
    @FXML private TableColumn<Rental,String> startDateColumn;
    @FXML private TableColumn<Rental,String> endDateColumn;
    @FXML private TableColumn<Rental,Float> totalCostColumn;

    // Insert Rental fields
    @FXML private TextField customerIdField, totalCostField;
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private ComboBox<Car> carComboBox;

    // Insert Car fields
    @FXML private TextField makeField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> availableComboBox;

    // Delete Car Fields
    @FXML private TextField deleteIdField;

    @FXML
    public void initialize() {
        // Initialize TableView columns for Car using the DTO Car class
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

        availableComboBox.setItems(FXCollections.observableArrayList("Yes", "No"));
        availableComboBox.getSelectionModel().selectFirst();

        // Initialize TableView columns for Rental using the DTO Rental class
        rentalIdColumn.setCellValueFactory(new PropertyValueFactory<>("rentalID"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        carIdRentalColumn.setCellValueFactory(new PropertyValueFactory<>("carID"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        // Formatter for totalCost column to show currency
        totalCostColumn.setCellFactory(column -> new TableCell<Rental, Float>() {
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

        try {
            MySqlCarDao carDao = new MySqlCarDao();
            List<Car> availableCars = carDao.getAvailableCars();
            carComboBox.setItems(FXCollections.observableArrayList(availableCars));
        } catch (DaoException e) {
            showAlert("Error", "Could not load available cars: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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
        insertCarPane.setVisible(false);
        deleteCarPane.setVisible(false);
        allCarsPane.setVisible(false);
        allRentalsPane.setVisible(false);
        insertRentalPane.setVisible(false);

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

    @FXML
    protected void handleShowInsertCar() {
        clearInsertFields();
        showPane(insertCarPane);
    }

    @FXML
    protected void handleInsertCar() {
        try {
            // Validate inputs
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            float price = Float.parseFloat(priceField.getText().trim());
            boolean available = availableComboBox.getValue().equals("Yes");

            if (make.isEmpty() || model.isEmpty()) {
                showAlert("Invalid Input", "Make and Model cannot be empty", Alert.AlertType.ERROR);
                return;
            }

            JSONObject carJson = new JSONObject();
            carJson.put("make", make);
            carJson.put("model", model);
            carJson.put("year", year);
            carJson.put("rentalPricePerDay", price);
            carJson.put("availability", available);

            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                dos.writeUTF("INSERT_CAR");
                dos.writeUTF(carJson.toString());

                String response = dis.readUTF();
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.has("error")) {
                    showAlert("Error", jsonResponse.getString("error"), Alert.AlertType.ERROR);
                } else {
                    showAlert("Success", "Car added successfully!", Alert.AlertType.INFORMATION);
                    clearInsertFields();
                    showPane(mainMenuPane);
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for Year and Price", Alert.AlertType.ERROR);
        } catch (IOException e) {
            showAlert("Connection Error", "Failed to connect to server: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to add car: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clearInsertFields() {
        makeField.clear();
        modelField.clear();
        yearField.clear();
        priceField.clear();
        availableComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    protected void handleShowDeleteCar() {
        deleteIdField.clear();
        showPane(deleteCarPane);
    }

    @FXML
    protected void handleDeleteCar() {
        String carIdStr = deleteIdField.getText().trim();

        if (carIdStr.isEmpty()) {
            showAlert("Invalid Input", "Please enter a Car ID", Alert.AlertType.ERROR);
            return;
        }

        try {
            int carId = Integer.parseInt(carIdStr);

            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                // Create JSON request
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("carID", carId);

                // Send command
                dos.writeUTF("DELETE_CAR");
                dos.writeUTF(jsonRequest.toString());

                // Handle response
                String response = dis.readUTF();
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.has("error")) {
                    showAlert("Error", jsonResponse.getString("error"), Alert.AlertType.ERROR);
                } else {
                    showAlert("Success", jsonResponse.getString("message"), Alert.AlertType.INFORMATION);
                    deleteIdField.clear();
                    showPane(mainMenuPane);
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number for Car ID", Alert.AlertType.ERROR);
        } catch (IOException e) {
            showAlert("Connection Error", "Failed to connect to server: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to delete car: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void handleViewDownloadImages() {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            dos.writeUTF("GET_IMAGES_LIST");

            String jsonResponse = dis.readUTF();
            JSONArray jsonArray = new JSONArray(jsonResponse);

            if (jsonArray.length() == 0) {
                showAlert("No Images", "No images available.", Alert.AlertType.INFORMATION);
                return;
            }

            // Build the list to show
            StringBuilder imagesList = new StringBuilder("Available Images:\n\n");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject img = jsonArray.getJSONObject(i);
                imagesList.append(String.format("%d. %s (Car ID: %d)\n",
                        i + 1,
                        img.getString("name"),
                        img.getInt("carID")));
            }

            // Create choice dialog with two options
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Download Options");
            alert.setHeaderText(imagesList.toString());
            alert.setContentText("Choose your download option:");

            ButtonType downloadOne = new ButtonType("Download One Image");
            ButtonType downloadAll = new ButtonType("Download All Images");
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(downloadOne, downloadAll, cancel);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent()) {
                if (result.get() == downloadOne) {
                    // Download one image
                    TextInputDialog inputDialog = new TextInputDialog();
                    inputDialog.setTitle("Download One Image");
                    inputDialog.setHeaderText(imagesList.toString());
                    inputDialog.setContentText("Enter image number:");

                    Optional<String> inputResult = inputDialog.showAndWait();
                    if (inputResult.isPresent()) {
                        try {
                            int choice = Integer.parseInt(inputResult.get());

                            if (choice > 0 && choice <= jsonArray.length()) {
                                String filename = jsonArray.getJSONObject(choice - 1).getString("filename");

                                if (downloadImage(filename)) {
                                    showAlert("Success", "Image downloaded successfully.", Alert.AlertType.INFORMATION);
                                } else {
                                    showAlert("Failed", "Image download failed.", Alert.AlertType.ERROR);
                                }
                            } else {
                                showAlert("Invalid Choice", "Invalid image number.", Alert.AlertType.WARNING);
                            }

                        } catch (NumberFormatException e) {
                            showAlert("Invalid Input", "Please enter a valid number.", Alert.AlertType.ERROR);
                        }
                    }

                } else if (result.get() == downloadAll) {
                    // Download all images
                    int successCount = 0, failCount = 0;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject img = jsonArray.getJSONObject(i);
                        String filename = img.getString("filename");

                        if (downloadImage(filename)) {
                            successCount++;
                        } else {
                            failCount++;
                        }
                    }

                    showAlert("Download Summary", String.format(
                            "Downloaded: %d\nFailed: %d", successCount, failCount), Alert.AlertType.INFORMATION);
                }
            }

        } catch (IOException e) {
            showAlert("Connection Error", "Failed to connect to server: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to process images: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean downloadImage(String filename) {
        try {
            File downloadsDir = new File("downloads");
            if (!downloadsDir.exists()) {
                downloadsDir.mkdir();
            }

            File outputFile = new File(downloadsDir, filename);

            // Check if file already exists
            if (outputFile.exists()) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("File Exists");
                confirmAlert.setHeaderText("The file already exists");
                confirmAlert.setContentText("Do you want to download and replace it?");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) {
                    // If user chooses not to overwrite, return true to prevent error message and exit
                    return true;
                }
            }

            // Only connect to server if we're actually going to download
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            dos.writeUTF("GET_IMAGE");
            dos.writeUTF(filename);

            boolean success = dis.readBoolean();
            if (!success) {
                socket.close();
                return false;
            }

            long fileSize = dis.readLong();

            try (FileOutputStream fos = new FileOutputStream(outputFile);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalRead = 0;

                while (totalRead < fileSize && (bytesRead = dis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
            }

            socket.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    protected void handleDisplayAllRentals() {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            dos.writeUTF("GET_ALL_RENTALS");
            String jsonResponse = dis.readUTF();

            JSONArray jsonArray = new JSONArray(jsonResponse);
            ObservableList<Rental> rentalsList = FXCollections.observableArrayList();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                Rental rental = new Rental(
                        o.getInt("rentalID"),
                        o.getInt("customerID"),
                        o.getInt("carID"),
                        LocalDate.parse(o.getString("startDate")),
                        LocalDate.parse(o.getString("endDate")),
                        (float)o.getDouble("totalCost")
                );
                rentalsList.add(rental);
            }

            rentalsTableView.setItems(rentalsList);
            showPane(allRentalsPane);

        } catch (IOException e) {
            showAlert("Connection Error", "Failed to connect to server: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to load rentals: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clearInsertRentalFields() {
        customerIdField.clear();
        carComboBox.getSelectionModel().clearSelection();
        totalCostField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    @FXML
    protected void handleShowInsertRental() {
        clearInsertRentalFields();
        // reload available cars each time the pane is shown
        try {
            MySqlCarDao carDao = new MySqlCarDao();
            List<Car> availableCars = carDao.getAvailableCars();
            carComboBox.setItems(FXCollections.observableArrayList(availableCars));
        } catch (DaoException e) {
            showAlert("Error", "Could not load available cars: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        showPane(insertRentalPane);
    }

    @FXML
    protected void handleInsertRental() {
        try {
            // Validate inputs
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            Car selected = carComboBox.getValue();
            if (selected == null) {
                showAlert("Invalid Input", "Please select a car.", Alert.AlertType.ERROR);
                return;
            }
            int carId = selected.getCarId();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            float totalCost = Float.parseFloat(totalCostField.getText().trim());

            // Validate date range
            if (startDate == null || endDate == null) {
                showAlert("Invalid Input", "Please select both start and end dates.", Alert.AlertType.ERROR);
                return;
            }

            // Check if end date is after start date
            if (endDate.isBefore(startDate)) {
                showAlert("Invalid Date Range", "End date must be after the start date.", Alert.AlertType.ERROR);
                return;
            }

            // Create JSON object for rental
            JSONObject rentalJson = new JSONObject();
            rentalJson.put("customerID", customerId);
            rentalJson.put("carID", carId);
            rentalJson.put("startDate", startDate.toString());
            rentalJson.put("endDate", endDate.toString());
            rentalJson.put("totalCost", totalCost);

            // Send the rental data to the server
            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                dos.writeUTF("INSERT_RENTAL");
                dos.writeUTF(rentalJson.toString());

                // Receive response from server
                String response = dis.readUTF();
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.has("error")) {
                    showAlert("Error", jsonResponse.getString("error"), Alert.AlertType.ERROR);
                } else {
                    showAlert("Success", "Rental added successfully!", Alert.AlertType.INFORMATION);
                    clearInsertRentalFields();
                    showPane(mainMenuPane);
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Customer ID, Car ID and Cost must be numbers.", Alert.AlertType.ERROR);
        } catch (IOException e) {
            showAlert("Connection Error", "Failed to connect to server: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}