-- Migration: chuyen bang Videos cu (VideoId VARCHAR) sang VideoId INT AUTO_INCREMENT + Shares/Likes.
-- Chay tren DB thien:  mysql -uthien -p thien < migrate-videos.sql
-- Bang Videos cu khong co du lieu can giu cho de thi nay nen drop va tao lai.
-- (Neu co du lieu can giu, hay backup truoc bang: mysqldump -uthien -p thien Videos > Videos.bak.sql)

USE thien;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS Videos;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE Videos (
    VideoId INT NOT NULL AUTO_INCREMENT,
    Active INT NOT NULL DEFAULT 1,
    Description VARCHAR(500) NULL,
    Poster VARCHAR(500) NULL,
    Title VARCHAR(500) NULL,
    Views INT NOT NULL DEFAULT 0,
    Shares INT NOT NULL DEFAULT 10,
    Likes INT NOT NULL DEFAULT 10,
    CategoryId INT NULL,
    PRIMARY KEY (VideoId),
    INDEX IX_Videos_CategoryId (CategoryId),
    CONSTRAINT FK_Videos_Categories
        FOREIGN KEY (CategoryId) REFERENCES categories (CategoryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
