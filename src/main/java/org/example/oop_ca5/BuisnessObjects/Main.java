package org.example.oop_ca5.BuisnessObjects;

import org.example.oop_ca5.DAOs.CarDaoInterface;
import org.example.oop_ca5.DAOs.MySqlCarDao;
import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.Exceptions.DaoException;

import java.util.List;
import java.util.Scanner;

public class Main {
    // create DAO objects
    public static CarDaoInterface carDao = new MySqlCarDao();
    public static void main(String[] args) throws DaoException {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n----- Car Rental Management System -----");
            System.out.println("1. View All Cars");
            System.out.println("2. Find Car by ID");
            System.out.println("3. Delete Car by ID");
            System.out.println("4. Insert a New Car");
            System.out.println("5. Update Car Details");
            System.out.println("6. Find Cars by Filter");
            System.out.println("7. Exit");

            int choice = -1; // ensure to get a valid value
            boolean validChoice = false;

            while (!validChoice) {
                System.out.print("Enter your choice: ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    if (choice >= 0 && choice <= 7) {
                        validChoice = true; // Valid choice
                    } else {
                        System.out.println("Invalid choice! Please enter a number between 0 and 7.");
                    }
                } else {
                    System.out.println("Invalid input! Please enter a number.");
                    scanner.next(); // Discard invalid input
                }
            }

            scanner.nextLine();

            switch (choice) {
                case 1:
                    loadAllCars(carDao);
                    break;
                case 2:
                    findCarById(scanner, carDao);
                    break;
                case 3:
                    deleteCarById(scanner, carDao);
                    break;
                case 4:
                    insertCar(scanner, carDao);
                    break;
                case 5:
                    updateCar(scanner, carDao);
                    break;
                case 6:
                    findCarsByFilter();
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

    private static void loadAllCars(CarDaoInterface carDao) {
        try {
            List<Car> carList = carDao.loadAllCars();
            System.out.println("\n--- All Cars ---");
            for (Car car : carList) {
                System.out.println(car);
            }
        } catch (DaoException e) {
            System.err.println("Error retrieving cars: " + e.getMessage());
        }
    }

    private static void findCarById(Scanner scanner, CarDaoInterface carDao) {
        System.out.println("Enter Car ID to find: ");
        int carID = validateCarID(scanner);
        try {
            Car car = carDao.findCarById(carID);
            System.out.println("Car found!");
            System.out.println(car.toString());
        } catch (DaoException e) {
            System.err.println("Error finding car: " + e.getMessage());
        }

    }

    private static void deleteCarById(Scanner scanner, CarDaoInterface carDao) {
        System.out.print("Enter Car ID to delete: ");
        int carID = validateCarID(scanner);
        try {
            carDao.deleteCarById(carID);
            System.out.println("Car with ID " + carID + " deleted successfully.");
        } catch (DaoException e) {
            System.err.println("Error deleting car: " + e.getMessage());
        }
    }

    private static void insertCar(Scanner scanner, CarDaoInterface carDao) {
        System.out.print("Enter Make: ");
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

    private static void updateCar(Scanner scanner, CarDaoInterface carDao) {
        System.out.println("Enter car ID to update: ");
        int carID = validateCarID(scanner);
        scanner.nextLine(); // consume newline character
        try {
            Car existingCar = carDao.findCarById(carID);
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid input! Please enter a number!");
        } catch (DaoException e) {
            System.err.println("Error finding car: " + e.getMessage());
        }
    }

    private static void findCarsByFilter() {
    }

    public static int validateCarID(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input! Please enter a number!");
            scanner.next();
        }
        return scanner.nextInt();
    }
}
