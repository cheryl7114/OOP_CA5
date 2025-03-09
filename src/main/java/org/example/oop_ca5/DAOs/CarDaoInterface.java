package org.example.oop_ca5.DAOs;
import org.example.oop_ca5.DTOs.Car;
import org.example.oop_ca5.Exceptions.DaoException;

import java.util.List;

public interface CarDaoInterface {

    List<Car> loadAllCars() throws DaoException;

    void deleteCarById(int carID) throws DaoException;

    Car insertCar(Car car) throws DaoException;
}
