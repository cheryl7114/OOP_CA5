package org.example.oop_ca5.DTOs;

public class ImageMetadata {
    private int id;
    private String name;
    private String filename;
    private int carID;

    public ImageMetadata() {}

    public ImageMetadata(int id, String name, String filename, int carID) {
        this.id = id;
        this.name = name;
        this.filename = filename;
        this.carID = carID;
    }

    // Getters and Setters
    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }

    public int getCarID() {
        return carID;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    @Override
    public String toString() {
        return "ImageMetadata{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", filename='" + filename + '\'' +
                ", carId=" + carID +
                '}';
    }
}
