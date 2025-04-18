package org.example.oop_ca5.DAOs;

import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.Exceptions.DaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MySqlCarDaoTest {
    CarDaoInterface carDao = null;

    @BeforeEach
    void setUp() {
        carDao = new MySqlCarDao();  // create the DAO class
    }

    @Test
    void testLoadAllCars() throws DaoException {
        List<Car> cars = carDao.loadAllCars();
        assertNotNull(cars);
        assertFalse(cars.isEmpty());
        // Check that each car has valid data
        for (Car car : cars) {
            assertTrue(car.getCarId() > 0);
            assertNotNull(car.getMake());
            assertNotNull(car.getModel());
            assertTrue(car.getYear() > 1900 && car.getYear() <= 2025);
            assertTrue(car.getRentalPricePerDay() > 0);
        }
    }

    @Test
    void testFindCarById() throws DaoException {
        // find car with id 1
        Car car = carDao.findCarById(1);
        assertNotNull(car);    // check that it did not return null
        assertEquals(1, car.getCarId());  // check that carId matches
        assertNotNull(car.getMake());
        assertNotNull(car.getModel());
        assertTrue(car.getYear() > 0);
        assertTrue(car.getRentalPricePerDay() > 0);
    }

    @Test
    void testFindCarByIdNotFound() {
        // test if finding a car that doesn't exist throws a DaoException
        Exception exception = assertThrows(DaoException.class, () -> {
            carDao.findCarById(999);
        });

        assertTrue(exception.getMessage().contains("No car found with ID: "));
    }

    @Test
    void testDeleteCarById() throws DaoException {
        // insert a test car
        Car testCar = new Car(0, "TestMake", "TestModel", 2023, 50, true);
        carDao.insertCar(testCar);

        // find the inserted car
        List<Car> cars = carDao.loadAllCars();
        Car insertedCar = null;
        for (Car car : cars) {
            if ("TestMake".equals(car.getMake()) && "TestModel".equals(car.getModel())) {
                insertedCar = car;
                break;
            }
        }
        assertNotNull(insertedCar);
        // delete the car
        int carIdToDelete = insertedCar.getCarId();
        carDao.deleteCarById(carIdToDelete);

        // verify deletion
        Exception exception = assertThrows(DaoException.class, () -> {
            carDao.findCarById(carIdToDelete);
        });
        assertTrue(exception.getMessage().contains("No car found with ID: "));
    }

    @Test
    void testDeleteCarByIdNotFound() {
        // test if deleting a car that doesn't exist throws a DaoException
        Exception exception = assertThrows(DaoException.class, () -> {
            carDao.deleteCarById(999);
        });

        assertTrue(exception.getMessage().contains("No car found with ID: "));
    }

    @Test
    void testInsertCar() throws DaoException {
        // insert test car
        Car testCar = new Car(0, "TestMake", "TestModel", 2023, 75, true);
        carDao.insertCar(testCar);

        // find the inserted car
        List<Car> allCars = carDao.loadAllCars();
        Car insertedCar = null;
        for (Car car : allCars) {
            if ("TestMake".equals(car.getMake()) && "TestModel".equals(car.getModel())) {
                insertedCar = car;
                break;
            }
        }
        // verify
        assertNotNull(insertedCar);
        assertEquals(2023, insertedCar.getYear());
        assertEquals(75, insertedCar.getRentalPricePerDay());

        // cleanup
        carDao.deleteCarById(insertedCar.getCarId());
    }

    @Test
    void testUpdateCar() throws DaoException {
        // get an existing car
        Car car = carDao.findCarById(1);
        String originalMake = car.getMake();
        float originalPrice = car.getRentalPricePerDay();

        // update make and rental price
        car.setMake("UpdatedMake");
        car.setRentalPricePerDay(originalPrice + 10);
        Car updatedCar = carDao.updateCar(car);

        // verify update
        assertEquals("UpdatedMake", updatedCar.getMake());
        assertEquals(originalPrice + 10, updatedCar.getRentalPricePerDay());

        // verify changes in DB
        Car dbCar = carDao.findCarById(1);
        assertEquals("UpdatedMake", dbCar.getMake());

        // Restore original values
        car.setMake(originalMake);
        car.setRentalPricePerDay(originalPrice);
        carDao.updateCar(car);
    }

    @Test
    void testUpdateCarNotFound() {
        Car testCar = new Car(999, "NoCar", "NoModel", 2023, 5, true);

        Exception exception = assertThrows(DaoException.class, () -> {
            carDao.updateCar(testCar);
        });

        assertTrue(exception.getMessage().contains("No car found with ID: "));
    }

    @Test
    void testGetAvailableCars() throws DaoException {
        List<Car> availableCars = carDao.getAvailableCars();

        assertNotNull(availableCars);

        // if availableCars List is empty, verify if all cars are really unavailable
        if (availableCars.isEmpty()) {
            List<Car> allCars = carDao.loadAllCars();
            for (Car car : allCars) {
                // car.isAvailable should be false
                assertFalse(car.isAvailable());
            }
        } else {
            for (Car car : availableCars) {
                // verify if the cars in the list are available
                assertTrue(car.isAvailable());
            }
        }
    }
}