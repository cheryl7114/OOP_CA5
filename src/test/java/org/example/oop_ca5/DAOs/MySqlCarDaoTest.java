package org.example.oop_ca5.DAOs;

import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.Exceptions.DaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySqlCarDaoTest {
    CarDaoInterface carDao = null;

    @BeforeEach
    void setUp() {
        carDao = new MySqlCarDao();  // create the DAO class
    }
}