-- X-Product ERP Database Schema
-- Run this script to initialize the database

CREATE DATABASE IF NOT EXISTS xproduct_db;
USE xproduct_db;

-- =============================================
-- BRANCHES TABLE
-- =============================================
CREATE TABLE IF NOT EXISTS branches (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    location   VARCHAR(200),
    code       VARCHAR(20) UNIQUE NOT NULL,
    active     BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =============================================
-- USERS TABLE
-- =============================================
CREATE TABLE IF NOT EXISTS users (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50) UNIQUE NOT NULL,
    password     VARCHAR(255) NOT NULL,
    full_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(100),
    phone        VARCHAR(20),
    role         ENUM('ADMIN', 'MANAGER', 'CLERK', 'ATTENDANT') NOT NULL,
    branch_id    BIGINT,
    created_by   BIGINT,
    active       BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_branch  FOREIGN KEY (branch_id)  REFERENCES branches(id),
    CONSTRAINT fk_user_creator FOREIGN KEY (created_by) REFERENCES users(id)
);

-- =============================================
-- SEED DATA
-- =============================================

-- Insert 3 branches
INSERT IGNORE INTO branches (name, location, code) VALUES
('Branch Alpha', 'Location A', 'BR-ALPHA'),
('Branch Beta',  'Location B', 'BR-BETA'),
('Branch Gamma', 'Location C', 'BR-GAMMA');

-- Insert default admin user (password: Admin@123)
-- Default admin password: Admin@123
-- Hash generated with BCrypt(strength=10)
INSERT IGNORE INTO users (username, password, full_name, email, role, active)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', 'admin@xproduct.com', 'ADMIN', TRUE);
