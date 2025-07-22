-- Tạo database
CREATE DATABASE space;
USE space;

CREATE TABLE spaceType (
    spaceTypeId INT AUTO_INCREMENT PRIMARY KEY,
    spaceTypeName VARCHAR(255) NOT NULL,
    spaceTypeLevel VARCHAR(255)
);

-- 3. Tạo bảng Space
CREATE TABLE space (
    spaceId INT AUTO_INCREMENT PRIMARY KEY,
    spaceName VARCHAR(255) NOT NULL,
    spaceTypeId INT NOT NULL,
    parentId INT DEFAULT NULL,
    FOREIGN KEY (spaceTypeId) REFERENCES spaceType(spaceTypeId),
    FOREIGN KEY (parentId) REFERENCES space(spaceId)
);


CREATE TABLE equipment (
    equipmentId INT AUTO_INCREMENT PRIMARY KEY,
    equipmentName VARCHAR(255) NOT NULL,
    spaceId INT,  
    FOREIGN KEY (spaceId) REFERENCES space(spaceId)
);