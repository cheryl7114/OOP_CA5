package org.example.oop_ca5.Utilities;

import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.Exceptions.DaoException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JSONConverterTest {

    private Car testCar;
    private List<Car> testCarList;

    @BeforeEach
    void setUp() {
        // Create a test car
        testCar = new Car(1, "Toyota", "Corolla", 2020, 50, true);

        // Create a list of test cars
        testCarList = new ArrayList<>();
        testCarList.add(testCar);
        testCarList.add(new Car(2, "Honda", "Civic", 2019, 45, false));
        testCarList.add(new Car(3, "Ford", "Focus", 2021, 55, true));
    }
}