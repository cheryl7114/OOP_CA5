package org.example.oop_ca5.DTOs;

public class Customer {
    private int customerID;
    private String customerName;
    private String email;
    private String phone;
    private String password;

    public Customer(int customerID, String customerName, String email, String phone, String password) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public Customer(String customerName, String email, String phone, String password) {
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public Customer() {}

    // Getters and Setters
    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Customer { ID: " + customerID + ", Name: " + customerName + ", Email: " + email + ", Phone: " + phone + " }";
    }
}
