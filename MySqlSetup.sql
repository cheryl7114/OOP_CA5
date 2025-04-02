CREATE DATABASE IF NOT EXISTS CarRental;
USE CarRental;

DROP TABLE IF EXISTS car;

CREATE TABLE car (
    carID INT AUTO_INCREMENT PRIMARY KEY,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INT NOT NULL,
    rentalPricePerDay FLOAT NOT NULL,
    availability BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE customer (
    customerID INT AUTO_INCREMENT PRIMARY KEY,
    customerName VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE rental (
    rentalID INT AUTO_INCREMENT PRIMARY KEY,
    customerID INT NOT NULL,
    carID INT NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    totalCost FLOAT NOT NULL,
    FOREIGN KEY (customerID) REFERENCES customer(customerID),
    FOREIGN KEY (carID) REFERENCES car(carID)
);


INSERT INTO car (make, model, year, rentalPricePerDay, availability) VALUES
('Toyota', 'Corolla', 2020, 45.99, TRUE),
('Honda', 'Civic', 2021, 50.00, TRUE),
('Ford', 'Focus', 2019, 40.00, FALSE),
('Chevrolet', 'Malibu', 2022, 55.00, TRUE),
('Nissan', 'Altima', 2020, 47.50, TRUE),
('BMW', '3 Series', 2021, 90.00, TRUE),
('Mercedes', 'C-Class', 2019, 95.00, FALSE),
('Audi', 'A4', 2022, 100.00, TRUE),
('Tesla', 'Model 3', 2021, 120.00, TRUE),
('Hyundai', 'Elantra', 2020, 42.00, TRUE);


-- hashing to be done later on
INSERT INTO customer (customerName, email, phone, password) VALUES
('John Doe', 'john.doe@gmail.com', '0871234567', 'password1'),
('Jane Smith', 'jane.smith@yahoo.com', '0872345678', 'password2'),
('Alice Johnson', 'alice.johnson@outlook.com', '0873456789', 'password3'),
('Bob Williams', 'bob.williams@gmail.com', '0874567890', 'password4'),
('Charlie Brown', 'charlie.brown@hotmail.com', '0875678901', 'password5'),
('David Clark', 'david.clark@yahoo.com', '0876789012', 'password6'),
('Emma Watson', 'emma.watson@outlook.com', '0877890123', 'password7'),
('Frank White', 'frank.white@gmail.com', '0878901234', 'password8'),
('Grace Hall', 'grace.hall@yahoo.com', '0879012345', 'password9'),
('Hannah King', 'hannah.king@outlook.com', '0879123456', 'password10');

