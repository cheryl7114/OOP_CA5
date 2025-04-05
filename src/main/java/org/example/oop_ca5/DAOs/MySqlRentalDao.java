package org.example.oop_ca5.DAOs;

import org.example.oop_ca5.DTOs.Rental;
import org.example.oop_ca5.Exceptions.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlRentalDao extends MySqlDao implements RentalDaoInterface {
    // Load all rentals
    @Override
    public List<Rental> loadAllRentals() throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Rental> rentalList = new ArrayList<>();

        try {
            connection = this.getConnection();

            String query = "SELECT * FROM rental";
            preparedStatement = connection.prepareStatement(query);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int rentalID = resultSet.getInt("rentalID");
                int customerID = resultSet.getInt("customerID");
                int carID = resultSet.getInt("carID");
                Date startDate = resultSet.getDate("startDate");
                Date endDate = resultSet.getDate("endDate");
                float totalCost = resultSet.getFloat("totalCost");

                // convert SQL Date to LocalDate before adding to rentalList
                Rental rental = new Rental(rentalID, customerID, carID, startDate.toLocalDate(), endDate.toLocalDate(), totalCost);
                rentalList.add(rental);
            }
        } catch (SQLException e) {
            throw new DaoException("loadAllRentals() " + e.getMessage());
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
                throw new DaoException("loadAllRentals() closing resources " + e.getMessage());
            }
        }

        return rentalList;
    }

    @Override
    public void insertRental(Rental rental) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.getConnection();

            String query = "INSERT INTO rental (customerID, carID, startDate, endDate, totalCost) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, rental.getCustomerID());
            preparedStatement.setInt(2, rental.getCarID());
            preparedStatement.setDate(3, Date.valueOf(rental.getStartDate())); // Convert LocalDate to SQL Date
            preparedStatement.setDate(4, Date.valueOf(rental.getEndDate()));   // Convert LocalDate to SQL Date
            preparedStatement.setDouble(5, rental.getTotalCost());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("addRental() " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    freeConnection(connection);
                }
            } catch (SQLException e) {
                throw new DaoException("addRental() closing resources " + e.getMessage());
            }
        }
    }
}
