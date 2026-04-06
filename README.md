# Order Management System

A Java Swing desktop application for managing clients, products, and orders, backed by a MySQL database. Built with a 3-layer architecture (Presentation, Business Logic, Data Access).

## Features

- **Client Management** – Add, edit, delete, and view clients
- **Product Management** – Manage product inventory with real-time stock tracking
- **Order Management** – Place orders with automatic stock validation and bill generation
- **Bill Tracking** – Auto-generated bills stored in the database upon order completion

## Architecture

```
├── Model/              # Plain Java objects (Client, Product, Order, Bill)
├── DataAccess/         # Generic AbstractDAO using Java Reflection for CRUD operations
├── BusinessLogic/      # Validation and business rules (stock checks, bill generation)
├── Presentation/       # Java Swing UI panels (ClientPanel, ProductPanel, OrderPanel, BillPanel)
└── Connection/         # MySQL connection factory
```

The `AbstractDAO<T>` class uses **Java Reflection** to dynamically build SQL queries (INSERT, UPDATE, SELECT, DELETE) for any model type, avoiding boilerplate DAO code.

## Tech Stack

- **Language:** Java
- **UI:** Java Swing
- **Database:** MySQL
- **Architecture:** 3-layer (Presentation / Business Logic / Data Access)


## Setup

1. Clone the repository
   ```bash
   git clone https://github.com/D-Mihai04/<repo-name>.git
   ```

2. Create the database and tables in MySQL:
   ```sql
   CREATE DATABASE order_management;
   USE order_management;

   CREATE TABLE Client (
       id INT AUTO_INCREMENT PRIMARY KEY,
       first_name VARCHAR(50),
       last_name VARCHAR(50),
       email VARCHAR(100),
       age INT
   );

   CREATE TABLE Product (
       id INT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(100),
       price DOUBLE,
       quantity INT
   );

   CREATE TABLE `Order` (
       id INT AUTO_INCREMENT PRIMARY KEY,
       client_id INT,
       order_date DATETIME,
       status VARCHAR(50),
       total DOUBLE
   );

   CREATE TABLE Bill (
       id INT AUTO_INCREMENT PRIMARY KEY,
       order_id INT,
       client_id INT,
       client_name VARCHAR(100),
       product_name VARCHAR(100),
       quantity INT,
       total DOUBLE,
       order_date DATETIME
   );
   ```

3. Update the database credentials in `Connection/ConnectionFactory.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/order_management";
   private static final String USER = "your_username";
   private static final String PASSWORD = "your_password";
   ```

4. Run the application:
   ```bash
   # Compile and run via your IDE (IntelliJ IDEA recommended)
   # or via command line from the project root:
   javac -cp .:mysql-connector-j.jar $(find . -name "*.java")
   java -cp .:mysql-connector-j.jar org.example.Main
   ```

## Usage

On launch, the main window opens with tabs for **Clients**, **Products**, **Orders**, and **Bills**. Use the buttons at the bottom of each panel to add, edit, or delete records. Placing an order automatically checks stock availability and generates a bill entry.
