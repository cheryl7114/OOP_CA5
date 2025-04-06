package org.example.oop_ca5.DTOs;

import java.time.LocalDate;

public class Rental {
    private int rentalID;
    private int customerID;
    private int carID;
    private LocalDate startDate;
    private LocalDate endDate;
    private float totalCost;

    public Rental(int rentalID, int customerID, int carID, LocalDate startDate, LocalDate endDate, float totalCost) {
        this.rentalID = rentalID;
        this.customerID = customerID;
        this.carID = carID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
    }

    public Rental(int customerID, int carID, LocalDate startDate, LocalDate endDate, float totalCost) {
        this.customerID = customerID;
        this.carID = carID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
    }

    public Rental() {}

    // getters and setters
    public int getRentalID() {
        return rentalID;
    }

    public void setRentalID(int rentalID) {
        this.rentalID = rentalID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getCarID() {
        return carID;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public String toString() {
        return "Rental { rentalID: " + rentalID + ", customerID: " + customerID + ", carID: " + carID
                + ", startDate: " + startDate + ", endDate: " + endDate + ", totalCost: " + totalCost + " }";
    }
}
