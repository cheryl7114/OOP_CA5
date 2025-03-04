package org.example.oop_ca5.DAOs;

import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.Exceptions.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlCarDao extends MySqlDao {
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

                Car car = new Car();
                carList.add(car);
            }
        } catch (SQLException e) {
            throw new DaoException("loadAllIncomeResultSet() " + e.getMessage());
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
                throw new DaoException("loadAllCars() " + e.getMessage());
            }
        }

        return carList;
    }
}
