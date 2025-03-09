package org.example.oop_ca5.BusinessObjects;

import org.example.oop_ca5.DAOs.CarDaoInterface;
import org.example.oop_ca5.DAOs.MySqlCarDao;
import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.Exceptions.DaoException;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static CarDaoInterface carDao = new MySqlCarDao();

    public static void main(String[] args) throws DaoException {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        // Load all cars from the database
        List<Car> carList = loadAllCars();

        while (!exit) {
            displayMenu();
            int choice = getValidChoice(scanner);
            scanner.nextLine();

            switch (choice) {
                case 1:
                    displayCarList(carList);
                    break;
                case 2:
                    findCarById(scanner);
                    break;
                case 3:
                    deleteCarById(scanner);
                    break;
                case 4:
                    insertCar(scanner);
                    break;
                case 5:
                    updateCar(scanner);
                    break;
                case 6:
                    List<Car> availableCars = getAvailableCars();
                    displayCarList(availableCars);
                    break;
                case 7:
                    exit = true;
                    System.out.println("Exiting Car Manager...");
                    break;
                default:
                    System.out.println("Invalid choice! Please enter a valid option.");
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n----- Car Rental Management System -----");
        System.out.println("1. View All Cars");
        System.out.println("2. Find Car by ID");
        System.out.println("3. Delete Car by ID");
        System.out.println("4. Insert a New Car");
        System.out.println("5. Update Car Details");
        System.out.println("6. Show Cars Available To Rent");
        System.out.println("7. Exit");
    }

    private static int getValidChoice(Scanner scanner) {
        int choice = -1;
        while (choice < 1 || choice > 7) {  // Keep looping until a valid choice is given
            System.out.print("\nEnter your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice < 1 || choice > 7) {
                    System.out.println("Invalid choice! Please enter a number between 1 and 7.");
                }
            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next(); // Discard invalid input
            }
        }
        return choice;
    }

    private static List<Car> loadAllCars() throws DaoException {
        try {
            return carDao.loadAllCars();
        } catch (DaoException e) {
            System.err.println("Error retrieving cars: " + e.getMessage());
            throw e;
        }
    }

    private static void displayCarList(List<Car> carList) {
        if (carList.isEmpty()) {
            System.out.println("\nNo cars found!");
        } else {
            for (Car car : carList) {
                System.out.println(car);
            }
        }
    }

    private static void findCarById(Scanner scanner) {
        System.out.print("\nEnter Car ID to find: ");
        int carID = validateCarID(scanner);
        try {
            Car car = carDao.findCarById(carID);
            System.out.println("Car found!");
            System.out.println(car.toString());
        } catch (DaoException e) {
            System.err.println("Error finding car: " + e.getMessage());
        }
    }

    private static void deleteCarById(Scanner scanner) {
        System.out.print("\nEnter Car ID to delete: ");
        int carID = validateCarID(scanner);
        try {
            carDao.deleteCarById(carID);
            System.out.println("Car with ID " + carID + " deleted successfully.");
        } catch (DaoException e) {
            System.err.println("Error deleting car: " + e.getMessage());
        }
    }

    private static void insertCar(Scanner scanner) {
        System.out.print("\nEnter Make: ");
        String make = scanner.nextLine();
        System.out.print("Enter Model: ");
        String model = scanner.nextLine();
        System.out.print("Enter Year: ");
        int year = scanner.nextInt();
        System.out.print("Enter Rental Price Per Day: ");
        float rentalPrice = scanner.nextFloat();
        System.out.print("Is the car available? (true/false): ");
        boolean availability = scanner.nextBoolean();

        Car newCar = new Car(0, make, model, year, rentalPrice, availability);
        try {
            Car insertedCar = carDao.insertCar(newCar);
            System.out.println("Inserted Car: " + insertedCar);
        } catch (DaoException e) {
            System.err.println("Error inserting car: " + e.getMessage());
        }
    }

    private static void updateCar(Scanner scanner) {
        System.out.println("\nEnter car ID to update: ");
        int carID = validateCarID(scanner);
        scanner.nextLine(); // consume newline character
        try {
            Car existingCar = carDao.findCarById(carID);

            System.out.println("Current car details: ");
            System.out.println(existingCar.toString());
            System.out.println("\n Leave any blank to keep the existing value");

            // if input is empty, keep the existing value else get the updated value
            System.out.println(" Car Make [" + existingCar.getMake() + "]: ");
            String makeInput = scanner.nextLine();
            String make = makeInput.isEmpty() ? existingCar.getMake() : makeInput;

            System.out.println(" Car Model [" + existingCar.getModel() + "]: ");
            String modelInput  = scanner.nextLine();
            String model = modelInput.isEmpty() ? existingCar.getModel() : modelInput;

            System.out.println(" Car Year [" + existingCar.getYear() + "]: ");
            String yearInput = scanner.nextLine();
            int year = yearInput.isEmpty() ? existingCar.getYear() : Integer.parseInt(yearInput);

            System.out.println(" Car Rental Price [" + existingCar.getRentalPricePerDay() + "]: ");
            String rentalPriceInput = scanner.nextLine();
            float rentalPrice = rentalPriceInput.isEmpty() ? existingCar.getRentalPricePerDay() : Float.parseFloat(rentalPriceInput);

            System.out.println(" Car Availability [" + existingCar.isAvailable() + "]: ");
            String availabilityInput = scanner.nextLine();
            boolean availability = availabilityInput.isEmpty() ? existingCar.isAvailable() : Boolean.parseBoolean(availabilityInput);

            Car updatedCar = new Car(carID, make, model, year, rentalPrice, availability);
            Car result = carDao.updateCar(updatedCar);

            System.out.println("Updated Car: " + result.toString());
        } catch (NumberFormatException e) {
            System.err.println("Invalid input! Please enter a number!");
        } catch (DaoException e) {
            System.err.println("Error finding car: " + e.getMessage());
        }
    }

    public static List<Car> getAvailableCars() throws DaoException {
        try {
            return carDao.getAvailableCars();
        } catch (DaoException e) {
            System.err.println("Error retrieving cars: " + e.getMessage());
            throw e;
        }
    }

    public static int validateCarID(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input! Please enter a number!");
            scanner.next();
        }
        return scanner.nextInt();
    }
}
