package org.example.oop_ca5.DAOs;

import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.Exceptions.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlCarDao extends MySqlDao implements CarDaoInterface {

    @Override
    public List<Car> loadAllCars() throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Car> carList = new ArrayList<>();

        try {
            // get connection using getConnection() method from MySqlDao.java
            connection = this.getConnection();

            String query = "SELECT * FROM car";
            preparedStatement = connection.prepareStatement(query);
            // execute query to get the result set
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int carID = resultSet.getInt("carID");
                String make = resultSet.getString("make");
                String model = resultSet.getString("model");
                int year = resultSet.getInt("year");
                float rentalPricePerDay = resultSet.getFloat("rentalPricePerDay");
                boolean availability = resultSet.getBoolean("availability");

                Car car = new Car(carID, make, model, year, rentalPricePerDay, availability);
                carList.add(car);
            }

        } catch (SQLException e) {
            throw new DaoException("loadAllCars() " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    freeConnection(connection);
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return carList;
    }

    @Override
    public void deleteCarById(int carID) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // get connection using getConnection() method from MySqlDao.java
            connection = this.getConnection();

            String query = "DELETE FROM car WHERE carID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, carID);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new DaoException("No car found with ID: " + carID);
            }

        } catch (SQLException e) {
            throw new DaoException("deleteCar() " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    freeConnection(connection);
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    @Override
    public Car insertCar(Car car) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = this.getConnection();

            String query = "INSERT INTO car (make, model, year, rentalPricePerDay, availability) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, car.getMake());
            preparedStatement.setString(2, car.getModel());
            preparedStatement.setInt(3, car.getYear());
            preparedStatement.setFloat(4, car.getRentalPricePerDay());
            preparedStatement.setBoolean(5, car.isAvailable());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new DaoException("No car was inserted.");
            }

            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                return new Car(generatedId, car.getMake(), car.getModel(), car.getYear(), car.getRentalPricePerDay(), car.isAvailable());
            } else {
                throw new DaoException("Failed to retrieve generated carID.");
            }

        } catch (SQLException e) {
            throw new DaoException("insertCar() " + e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    freeConnection(connection);
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
