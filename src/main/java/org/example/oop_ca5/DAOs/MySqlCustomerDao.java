package org.example.oop_ca5.DAOs;

import org.example.oop_ca5.DTOs.Customer;
import org.example.oop_ca5.Exceptions.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlCustomerDao extends MySqlDao implements CustomerDaoInterface {
    @Override
    public List<Customer> loadAllCustomers() throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Customer> customerList = new ArrayList<>();

        try {
            connection = this.getConnection();

            String query = "SELECT * FROM customer";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int customerID = resultSet.getInt("customerID");
                String customerName = resultSet.getString("customerName");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String password = resultSet.getString("password");

                Customer customer = new Customer(customerID, customerName, email, phone, password);
                customerList.add(customer);
            }

        } catch (SQLException e) {
            throw new DaoException("loadAllCustomers() " + e.getMessage());
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

        return customerList;
    }

    @Override
    public void insertCustomer(Customer customer) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.getConnection();

            String query = "INSERT INTO customer (customerName, email, phone, password) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);

            // set parameter values
            preparedStatement.setString(1, customer.getCustomerName());
            preparedStatement.setString(2, customer.getEmail());
            preparedStatement.setString(3, customer.getPhone());
            preparedStatement.setString(4, customer.getPassword());

            // execute update
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("insertCustomer() " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    freeConnection(connection);
                }
            } catch (SQLException e) {
                throw new DaoException("insertCustomer() closing resources " + e.getMessage());
            }
        }
    }
}
