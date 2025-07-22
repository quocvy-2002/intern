-- Tạo database
CREATE DATABASE space;
USE space;

-- Tạo bảng spaceType trước vì bảng space tham chiếu đến nó
CREATE TABLE spaceType (
    spaceTypeId INT AUTO_INCREMENT PRIMARY KEY,
    spaceTypeName VARCHAR(255) NOT NULL,
    spaceTypeLevel VARCHAR(255)
);

-- Tạo bảng space
CREATE TABLE space (
    spaceId INT AUTO_INCREMENT PRIMARY KEY,
    spaceName VARCHAR(255) NOT NULL,
    spaceTypeName VARCHAR(255) NOT NULL,
    spaceTypeLevel VARCHAR(255) NOT NULL,
    parentId INT ,
    FOREIGN KEY (spaceTypeName) REFERENCES spaceType(spaceTypeName),
    FOREIGN KEY (spaceTypeLevel) REFERENCES spaceType(spaceTypeLevel),
    FOREIGN KEY (parentId) REFERENCES space(spaceId)
);

-- Chèn dữ liệu vào bảng spaceType
INSERT INTO spaceType (spaceTypeName, spaceTypeLevel)
VALUES ('ROOM', '2'),
       ('SPACE', NULL),
       ('FLOOR', '1');

-- Kiểm tra dữ liệu trong bảng spaceType
SELECT * FROM spaceType;

-- Kiểm tra dữ liệu trong bảng space (hiện tại chưa có dữ liệu)
SELECT * FROM space;