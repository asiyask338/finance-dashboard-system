-- SQL script to create the database schema for the Finance Dashboard application
CREATE DATABASE finance_dashboard;

USE finance_dashboard;

-- Table to store user roles
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Table to store user information
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255),
    role_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_role FOREIGN KEY (role_id)
    REFERENCES roles(id)
);

-- Table to store financial records (income and expenses)
CREATE TABLE financial_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    amount DECIMAL(12,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    category VARCHAR(100),
    record_date DATE NOT NULL,
    notes VARCHAR(255),

    user_id BIGINT NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_record_user FOREIGN KEY (user_id)
    REFERENCES users(id)
);

-- Indexes for performance optimization
CREATE INDEX idx_user_id ON financial_records(user_id);