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

    @Test
    void testCarToJSONObject() {
        JSONObject jsonObject = JSONConverter.carToJSONObject(testCar);

        assertEquals(1, jsonObject.getInt("carID"));
        assertEquals("Toyota", jsonObject.getString("make"));
        assertEquals("Corolla", jsonObject.getString("model"));
        assertEquals(2020, jsonObject.getInt("year"));
        assertEquals(50, jsonObject.getDouble("rentalPricePerDay"));
        assertTrue(jsonObject.getBoolean("availability"));
    }

    @Test
    void testCarObjectToJSONString() throws DaoException {
        String jsonString = JSONConverter.carObjectToJSONString(testCar);

        // Parse the string back to a JSONObject to verify contents
        JSONObject jsonObject = new JSONObject(jsonString);

        assertEquals(1, jsonObject.getInt("carID"));
        assertEquals("Toyota", jsonObject.getString("make"));
        assertEquals("Corolla", jsonObject.getString("model"));
        assertEquals(2020, jsonObject.getInt("year"));
        assertEquals(50, jsonObject.getDouble("rentalPricePerDay"));
        assertTrue(jsonObject.getBoolean("availability"));
    }

    @Test
    void testCarListToJSONString() throws DaoException {
        String jsonString = JSONConverter.carListToJSONString(testCarList);

        // Parse the string back to a JSONArray to verify contents
        JSONArray jsonArray = new JSONArray(jsonString);

        assertEquals(3, jsonArray.length());

        // Verify first car
        JSONObject firstCar = jsonArray.getJSONObject(0);
        assertEquals(1, firstCar.getInt("carID"));
        assertEquals("Toyota", firstCar.getString("make"));

        // Verify second car
        JSONObject secondCar = jsonArray.getJSONObject(1);
        assertEquals(2, secondCar.getInt("carID"));
        assertEquals("Honda", secondCar.getString("make"));
        assertEquals(false, secondCar.getBoolean("availability"));

        // Verify third car
        JSONObject thirdCar = jsonArray.getJSONObject(2);
        assertEquals(3, thirdCar.getInt("carID"));
        assertEquals("Ford", thirdCar.getString("make"));
    }
}