-- Create tables with proper structure matching Hibernate expectations

-- Users table
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    verification_token_expiry DATETIME(6),
    reset_token VARCHAR(255),
    reset_token_expiry DATETIME(6),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
    );

-- User_roles table - SIMPLIFIED för @ElementCollection
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255)
    );

-- Products table
CREATE TABLE IF NOT EXISTS products (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10,2) NOT NULL,
    sale_price DECIMAL(10,2),
    stock_quantity INTEGER DEFAULT 0,
    category VARCHAR(255),
    category_id INTEGER,
    manufacturer_id INTEGER,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    view_count INTEGER DEFAULT 0,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
    );

-- Carts table
CREATE TABLE IF NOT EXISTS carts (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     user_id BIGINT,
                                     session_id VARCHAR(255),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Cart_items table
CREATE TABLE IF NOT EXISTS cart_items (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          cart_id BIGINT,
                                          product_id BIGINT NOT NULL,
                                          quantity INTEGER NOT NULL DEFAULT 1,
                                          unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      user_id BIGINT,
                                      order_number VARCHAR(255) NOT NULL UNIQUE,
    order_date DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    total_amount DOUBLE NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    payment_status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED', 'PARTIALLY_REFUNDED'),
    payment_method VARCHAR(255),
    payment_transaction_id VARCHAR(255),
    transaction_id VARCHAR(255),
    delivery_name VARCHAR(255) NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    delivery_city VARCHAR(255) NOT NULL,
    delivery_postal_code VARCHAR(255) NOT NULL,
    delivery_phone VARCHAR(255),
    refunded_at DATETIME(6),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
    );

-- Order_items table
CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           order_id BIGINT,
                                           product_id BIGINT,
                                           product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    price DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL
    );

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        order_id BIGINT NOT NULL,
                                        amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED', 'PARTIALLY_REFUNDED') NOT NULL,
    type ENUM('CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'CASH', 'STRIPE', 'MOBILE_PAYMENT', 'CRYPTOCURRENCY', 'GIFT_CARD', 'REFUND'),
    transaction_id VARCHAR(100),
    gateway_transaction_id VARCHAR(100),
    authorization_code VARCHAR(50),
    merchant_reference VARCHAR(100),
    card_type VARCHAR(20),
    masked_card_number VARCHAR(20),
    gateway_response VARCHAR(1000),
    failure_reason VARCHAR(500),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    processed_at DATETIME(6),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    );

-- Customer_entity table (if needed)
CREATE TABLE IF NOT EXISTS customer_entity (
                                               id BIGINT PRIMARY KEY,
                                               username VARCHAR(255),
    password VARCHAR(255)
    );