package org.example.oop_ca5.Utilities;

import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.Exceptions.DaoException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;


public class JSONConverter {
    // Convert list of car objects to JSON string
    public static String carListToJSONString(List<Car> carList) throws DaoException {
        try {
            JSONArray jsonArray = new JSONArray();
            
            for (Car car : carList) {
                // convert each car to json object
                JSONObject carJSONObject = new JSONObject();
                carJSONObject.put("carId", car.getCarId());
                carJSONObject.put("make", car.getMake());
                carJSONObject.put("model", car.getModel());
                carJSONObject.put("year", car.getYear());
                carJSONObject.put("rentalPricePerDay", car.getRentalPricePerDay());
                carJSONObject.put("availability", car.isAvailable());

                jsonArray.put(carJSONObject);
            }
            // return as a json string
            return jsonArray.toString(4);
        } catch (Exception e) {
            throw new DaoException("Error converting car list to JSON");
        }
    }
}
