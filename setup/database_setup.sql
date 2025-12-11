-- Run this script in MySQL Workbench or MySQL Command Line Client

-- 1. Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS life_analytics_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 2. Create a user (Optional: You can use 'root' instead)
-- Uncomment the lines below if you want to create a specific user for this app
-- CREATE USER 'dali'@'localhost' IDENTIFIED BY 'secure_password';
-- GRANT ALL PRIVILEGES ON life_analytics_db.* TO 'dali'@'localhost';
-- FLUSH PRIVILEGES;

USE life_analytics_db;

-- Tables will be automatically created by Spring Boot (Hibernate) 
-- when you run the application because of:
-- spring.jpa.hibernate.ddl-auto=update
