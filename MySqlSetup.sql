CREATE DATABASE IF NOT EXISTS CarRental;
USE CarRental;

DROP TABLE IF EXISTS Car;

CREATE TABLE Car (
    carID INT AUTO_INCREMENT PRIMARY KEY,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INT NOT NULL,
    rentalPricePerDay FLOAT NOT NULL,
    availability BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO Car (make, model, year, rentalPricePerDay, availability) VALUES
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
