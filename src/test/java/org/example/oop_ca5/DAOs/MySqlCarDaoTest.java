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