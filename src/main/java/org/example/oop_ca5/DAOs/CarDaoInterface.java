package org.example.oop_ca5.DAOs;
import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.Exceptions.DaoException;

import java.util.List;

public interface CarDaoInterface {
    List<Car> loadAllCars() throws DaoException;
    Car findCarById(int carID) throws DaoException;
    void deleteCarById(int carID) throws DaoException;
    void insertCar(Car car) throws DaoException;
    Car updateCar(Car car) throws DaoException;
    List<Car> getAvailableCars() throws DaoException;
}
