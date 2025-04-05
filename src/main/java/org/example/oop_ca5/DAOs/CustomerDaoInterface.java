package org.example.oop_ca5.DAOs;

import org.example.oop_ca5.DTOs.Customer;
import org.example.oop_ca5.Exceptions.DaoException;

import java.util.List;

public interface CustomerDaoInterface {
    List<Customer> loadAllCustomers() throws DaoException;
    void insertCustomer(Customer customer) throws DaoException;
}
