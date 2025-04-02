package org.example.oop_ca5.DAOs;

import org.example.oop_ca5.DTOs.Rental;
import org.example.oop_ca5.Exceptions.DaoException;

import java.util.List;

public interface RentalDaoInterface {
    List<Rental> loadAllRentals() throws DaoException;
    void insertRental(Rental rental) throws DaoException;
}
