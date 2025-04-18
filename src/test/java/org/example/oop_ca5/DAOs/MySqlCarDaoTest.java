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


}