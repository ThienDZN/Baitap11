CREATE DATABASE IF NOT EXISTS baitap11
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE baitap11;

CREATE TABLE IF NOT EXISTS categories (
    CategoryId INT NOT NULL AUTO_INCREMENT,
    CategoryName VARCHAR(50) NOT NULL,
    Images VARCHAR(500) NULL,
    Status INT NOT NULL DEFAULT 1,
    PRIMARY KEY (CategoryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS users (
    UserId BIGINT NOT NULL AUTO_INCREMENT,
    FullName VARCHAR(120) NOT NULL,
    Username VARCHAR(50) NOT NULL,
    Email VARCHAR(120) NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    Phone VARCHAR(20) NULL,
    Images VARCHAR(500) NULL,
    RoleName VARCHAR(20) NOT NULL DEFAULT 'USER',
    Enabled TINYINT(1) NOT NULL DEFAULT 0,
    Status INT NOT NULL DEFAULT 1,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (UserId),
    CONSTRAINT UQ_users_Username UNIQUE (Username),
    CONSTRAINT UQ_users_Email UNIQUE (Email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Videos (
    VideoId VARCHAR(50) NOT NULL,
    Active INT NOT NULL DEFAULT 1,
    Description VARCHAR(500) NULL,
    Poster VARCHAR(500) NULL,
    Title VARCHAR(500) NULL,
    Views INT NOT NULL DEFAULT 0,
    CategoryId INT NULL,
    PRIMARY KEY (VideoId),
    INDEX IX_Videos_CategoryId (CategoryId),
    CONSTRAINT FK_Videos_Categories
        FOREIGN KEY (CategoryId) REFERENCES categories (CategoryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS otp_verifications (
    OtpId BIGINT NOT NULL AUTO_INCREMENT,
    UserId BIGINT NULL,
    Email VARCHAR(120) NOT NULL,
    OtpCode VARCHAR(10) NOT NULL,
    Purpose VARCHAR(30) NOT NULL,
    ExpiryAt DATETIME NOT NULL,
    Used TINYINT(1) NOT NULL DEFAULT 0,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (OtpId),
    INDEX IX_otp_verifications_EmailPurpose (Email, Purpose),
    INDEX IX_otp_verifications_UserId (UserId),
    CONSTRAINT FK_otp_verifications_users
        FOREIGN KEY (UserId) REFERENCES users (UserId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS products (
    ProductId BIGINT NOT NULL AUTO_INCREMENT,
    ProductName VARCHAR(150) NOT NULL,
    Description VARCHAR(2000) NULL,
    Price DECIMAL(18,2) NOT NULL,
    Quantity INT NOT NULL DEFAULT 0,
    Image VARCHAR(500) NULL,
    Status INT NOT NULL DEFAULT 1,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CategoryId INT NOT NULL,
    PRIMARY KEY (ProductId),
    INDEX IX_products_CategoryId (CategoryId),
    CONSTRAINT FK_products_categories
        FOREIGN KEY (CategoryId) REFERENCES categories (CategoryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS customer_orders (
    OrderId BIGINT NOT NULL AUTO_INCREMENT,
    UserId BIGINT NOT NULL,
    RecipientName VARCHAR(120) NOT NULL,
    Phone VARCHAR(20) NOT NULL,
    ShippingAddress VARCHAR(500) NOT NULL,
    PaymentMethod VARCHAR(20) NOT NULL,
    PaymentStatus VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    Status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    TotalAmount DECIMAL(18,2) NOT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (OrderId),
    INDEX IX_customer_orders_UserStatus (UserId, Status),
    CONSTRAINT CHK_customer_orders_Status CHECK (Status IN ('NEW', 'CONFIRMED', 'PREPARING', 'SHIPPING', 'DELIVERING', 'DELIVERED', 'CANCELLED', 'RETURNED')),
    CONSTRAINT FK_customer_orders_users
        FOREIGN KEY (UserId) REFERENCES users (UserId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS order_items (
    OrderItemId BIGINT NOT NULL AUTO_INCREMENT,
    OrderId BIGINT NOT NULL,
    ProductId BIGINT NOT NULL,
    ProductName VARCHAR(150) NOT NULL,
    UnitPrice DECIMAL(18,2) NOT NULL,
    Quantity INT NOT NULL,
    LineTotal DECIMAL(18,2) NOT NULL,
    PRIMARY KEY (OrderItemId),
    INDEX IX_order_items_OrderId (OrderId),
    INDEX IX_order_items_ProductId (ProductId),
    CONSTRAINT FK_order_items_customer_orders
        FOREIGN KEY (OrderId) REFERENCES customer_orders (OrderId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
