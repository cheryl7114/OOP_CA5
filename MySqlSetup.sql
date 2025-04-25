CREATE DATABASE IF NOT EXISTS CarRental;
USE CarRental;

DROP TABLE IF EXISTS rental;
DROP TABLE IF EXISTS image;
DROP TABLE IF EXISTS car;
DROP TABLE IF EXISTS customer;

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

CREATE TABLE image (
    imageID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    carID INT NOT NULL,
    FOREIGN KEY (carID) REFERENCES car(carID)
);

INSERT INTO car (make, model, year, rentalPricePerDay, availability) VALUES
('Toyota', 'Corolla', 2020, 45.99, TRUE),   -- Available
('Honda', 'Civic', 2021, 50.00, FALSE),      -- Rented by Customer 2
('Ford', 'Focus', 2019, 40.00, TRUE),       -- Available
('Chevrolet', 'Malibu', 2022, 55.00, TRUE), -- Available
('Nissan', 'Altima', 2020, 47.50, TRUE),   -- Available
('BMW', '3 Series', 2021, 90.00, FALSE),    -- Rented by Customer 5
('Mercedes', 'C-Class', 2019, 95.00, TRUE), -- Available
('Audi', 'A4', 2022, 100.00, FALSE),        -- Rented by Customer 8
('Tesla', 'Model 3', 2021, 120.00, FALSE),  -- Rented by Customer 9
('Hyundai', 'Elantra', 2020, 42.00, TRUE);  -- Available

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

INSERT INTO rental (customerID, carID, startDate, endDate, totalCost) VALUES
(2, 2, '2025-04-01', '2025-04-12', 50.00 * 12),  -- Honda Civic (12 days)
(5, 6, '2025-04-01', '2025-04-08', 90.00 * 8),   -- BMW 3 Series (8 days)
(8, 8, '2025-03-30', '2025-04-03', 100.00 * 5),  -- Audi A4 (5 days)
(9, 9, '2025-03-28', '2025-04-10', 120.00 * 14), -- Tesla Model 3 (14 days)
(1, 2, '2025-01-10', '2025-01-15', 50.00 * 6),   -- Honda Civic (6 days)
(3, 1, '2024-11-25', '2024-11-27', 45.99 * 3),   -- Toyota Corolla (3 days)
(4, 10, '2024-12-05', '2024-12-09', 42.00 * 5),  -- Hyundai Elantra (5 days)
(6, 2, '2024-11-10', '2024-11-19', 50.00 * 10),  -- Honda Civic (10 days)
(7, 7, '2025-02-15', '2025-02-20', 95.00 * 6),   -- Mercedes C-Class (6 days)
(10, 10, '2024-12-20', '2024-12-24', 42.00 * 5); -- Hyundai Elantra (5 days)
