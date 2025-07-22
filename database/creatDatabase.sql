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
ALTER TABLE equipment 
ADD COLUMN equipmentTypeId INT,
ADD FOREIGN KEY (equipmentTypeId) REFERENCES equipment_type(equipmentTypeId);
CREATE TABLE equipment_type (
    equipmentTypeId INT AUTO_INCREMENT PRIMARY KEY,
    equipmentTypeName VARCHAR(255) NOT NULL 
);

CREATE TABLE equipment_status_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    equipmentId INT NOT NULL,
    timestamp DATETIME NOT NULL,
    status VARCHAR(255)  NOT NULL,
    powerConsumptionKW DECIMAL(10,2),
    FOREIGN KEY (equipmentId) REFERENCES equipment(equipmentId)
);
CREATE TABLE equipment_usage_history (
    historyId INT AUTO_INCREMENT PRIMARY KEY,
    equipmentId INT NOT NULL,
    startTime DATETIME NOT NULL,
    endTime DATETIME NOT NULL,
    totalPowerConsumption DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (equipmentId) REFERENCES equipment(equipmentId)
);