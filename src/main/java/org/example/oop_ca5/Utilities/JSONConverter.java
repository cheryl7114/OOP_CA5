package org.example.oop_ca5.Utilities;

import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.DTOs.ImageMetadata;
import org.example.oop_ca5.Exceptions.DaoException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class JSONConverter {
    // Feature 7: Convert list of car objects to JSON string
    public static String carListToJSONString(List<Car> carList) throws DaoException {
        try {
            JSONArray jsonArray = new JSONArray();

            for (Car car : carList) {
                // convert each car to json object
                JSONObject carJSONObject = carToJSONObject(car);
                jsonArray.put(carJSONObject);
            }
            // return as a json string
            return jsonArray.toString(4);
        } catch (Exception e) {
            throw new DaoException("Error converting car list to JSON " + e.getMessage());
        }
    }

    // Feature 8: Convert single car object to JSON string
    public static String carObjectToJSONString(Car car) throws DaoException {
        try {
            JSONObject carJSONObject = carToJSONObject(car);

            return carJSONObject.toString(4);
        } catch (Exception e) {
            throw new DaoException("Error converting car to JSON " + e.getMessage());
        }
    }

    // method to convert car object to json object
    public static JSONObject carToJSONObject(Car car) {
        JSONObject carJson = new JSONObject();
        carJson.put("carID", car.getCarId());
        carJson.put("make", car.getMake());
        carJson.put("model", car.getModel());
        carJson.put("year", car.getYear());
        carJson.put("rentalPricePerDay", car.getRentalPricePerDay());
        carJson.put("availability", car.isAvailable());

        return carJson;
    }

    // convert list of image metadata objects to JSON string
    public static String imageListToJSONString(List<ImageMetadata> imagesList) throws DaoException {
        try {
            JSONArray jsonArray = new JSONArray();

            for (ImageMetadata img : imagesList) {
                JSONObject imgJSONObject = imageToJSONObject(img);
                jsonArray.put(imgJSONObject);
            }
            return jsonArray.toString(4);
        } catch (Exception e) {
            throw new DaoException("Error converting image list to JSON " + e.getMessage());
        }
    }

    // convert ImageMetadata to JSON object
    public static JSONObject imageToJSONObject(ImageMetadata image) {
        JSONObject jsonImage = new JSONObject();
        jsonImage.put("id", image.getID());
        jsonImage.put("name", image.getName());
        jsonImage.put("filename", image.getFilename());
        jsonImage.put("carID", image.getCarID());

        return jsonImage;
    }
}
