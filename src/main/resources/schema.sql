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
-- FUEL TYPES TABLE
-- =============================================
CREATE TABLE IF NOT EXISTS fuel_types (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(50) NOT NULL,
    code   VARCHAR(20) UNIQUE NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

-- =============================================
-- GRN TABLE (Goods Received Note)
-- =============================================
CREATE TABLE IF NOT EXISTS grn (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    grn_number       VARCHAR(50) UNIQUE NOT NULL,
    branch_id        BIGINT NOT NULL,
    fuel_type_id     BIGINT NOT NULL,
    quantity_liters  DECIMAL(10,2) NOT NULL,
    price_per_liter  DECIMAL(10,2) NOT NULL,
    total_amount     DECIMAL(12,2) NOT NULL,
    supplier         VARCHAR(100),
    received_date    DATE NOT NULL,
    notes            TEXT,
    created_by       BIGINT NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_grn_branch  FOREIGN KEY (branch_id)    REFERENCES branches(id),
    CONSTRAINT fk_grn_fuel    FOREIGN KEY (fuel_type_id) REFERENCES fuel_types(id),
    CONSTRAINT fk_grn_creator FOREIGN KEY (created_by)   REFERENCES users(id)
);

-- =============================================
-- STOCK TABLE (one row per branch + fuel type)
-- =============================================
CREATE TABLE IF NOT EXISTS stock (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_id        BIGINT NOT NULL,
    fuel_type_id     BIGINT NOT NULL,
    quantity_liters  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    last_updated     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_stock_branch_fuel (branch_id, fuel_type_id),
    CONSTRAINT fk_stock_branch FOREIGN KEY (branch_id)    REFERENCES branches(id),
    CONSTRAINT fk_stock_fuel   FOREIGN KEY (fuel_type_id) REFERENCES fuel_types(id)
);

-- =============================================
-- DAILY SALES TABLE
-- =============================================
CREATE TABLE IF NOT EXISTS daily_sales (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_date        DATE NOT NULL,
    branch_id        BIGINT NOT NULL,
    fuel_type_id     BIGINT NOT NULL,
    liters_sold      DECIMAL(10,2) NOT NULL,
    price_per_liter  DECIMAL(10,2) NOT NULL,
    total_amount     DECIMAL(12,2) NOT NULL,
    recorded_by      BIGINT NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sale_branch FOREIGN KEY (branch_id)    REFERENCES branches(id),
    CONSTRAINT fk_sale_fuel   FOREIGN KEY (fuel_type_id) REFERENCES fuel_types(id),
    CONSTRAINT fk_sale_user   FOREIGN KEY (recorded_by)  REFERENCES users(id)
);

-- =============================================
-- SEED DATA
-- =============================================

-- 3 Branches
INSERT IGNORE INTO branches (name, location, code) VALUES
('Branch Alpha', 'Location A', 'BR-ALPHA'),
('Branch Beta',  'Location B', 'BR-BETA'),
('Branch Gamma', 'Location C', 'BR-GAMMA');

-- 5 Fuel Types
INSERT IGNORE INTO fuel_types (name, code) VALUES
('92 Petrol',    'PETROL_92'),
('95 Petrol',    'PETROL_95'),
('Auto Diesel',  'DIESEL_AUTO'),
('Super Diesel', 'DIESEL_SUPER'),
('Kerosene',     'KEROSENE');

-- =============================================
-- ATTENDANCE TABLE
-- =============================================
CREATE TABLE IF NOT EXISTS attendance (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    attendant_id BIGINT NOT NULL,
    branch_id    BIGINT NOT NULL,
    clock_in     DATETIME NOT NULL,
    clock_out    DATETIME,
    status       ENUM('CLOCK_IN_PENDING','CLOCKED_IN','CLOCK_OUT_PENDING','COMPLETED','REJECTED')
                 NOT NULL DEFAULT 'CLOCK_IN_PENDING',
    approved_by  BIGINT,
    approved_at  DATETIME,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_att_attendant FOREIGN KEY (attendant_id) REFERENCES users(id),
    CONSTRAINT fk_att_branch    FOREIGN KEY (branch_id)    REFERENCES branches(id),
    CONSTRAINT fk_att_approver  FOREIGN KEY (approved_by)  REFERENCES users(id)
);

-- If table already exists with old enum, run this:
-- ALTER TABLE attendance MODIFY COLUMN status
--   ENUM('CLOCK_IN_PENDING','CLOCKED_IN','CLOCK_OUT_PENDING','COMPLETED','REJECTED')
--   NOT NULL DEFAULT 'CLOCK_IN_PENDING';
-- ALTER TABLE attendance CHANGE COLUMN login_time clock_in DATETIME NOT NULL;
-- ALTER TABLE attendance CHANGE COLUMN logout_time clock_out DATETIME;

-- Default admin user  (password: Admin@123)
INSERT IGNORE INTO users (username, password, full_name, email, role, active)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', 'admin@xproduct.com', 'ADMIN', TRUE);
