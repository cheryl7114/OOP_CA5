package org.example.oop_ca5.DTOs;

public class Car {
    private int id;
    private String make;
    private String model;
    private int year;
    private float rentalPricePerDay;
    private boolean availability;

    public Car(int id, String make, String model, int year, float rentalPricePerDay, boolean availability) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.rentalPricePerDay = rentalPricePerDay;
        this.availability = availability;
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public float getRentalPricePerDay() {
        return rentalPricePerDay;
    }

    public void setRentalPricePerDay(float rentalPricePerDay) {
        this.rentalPricePerDay = rentalPricePerDay;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
}

