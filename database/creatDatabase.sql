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
ALTER TABLE equipment_usage_history MODIFY endTime DATETIME NULL;
ALTER TABLE equipment_usage_history MODIFY totalPowerConsumption DATETIME NULL;

SELECT * FROM equipment_usage_history;

-- Trạng thái
CREATE TABLE equipment_status (
    statusId INT AUTO_INCREMENT PRIMARY KEY,
    statusName VARCHAR(255) NOT NULL
);

-- Nhà cung cấp
CREATE TABLE provider (
    providerId INT AUTO_INCREMENT PRIMARY KEY,
    providerName VARCHAR(255) NOT NULL
);

-- Giá trị thiết bị
CREATE TABLE equipment_value (
    equipmentValueId INT AUTO_INCREMENT PRIMARY KEY,
    equipmentValue DECIMAL(10,2) NOT NULL
);
ALTER TABLE equipment
ADD COLUMN statusId INT,
ADD COLUMN providerId INT,
ADD COLUMN equipmentValueId INT,
ADD FOREIGN KEY (statusId) REFERENCES equipment_status(statusId),
ADD FOREIGN KEY (providerId) REFERENCES provider(providerId),
ADD FOREIGN KEY (equipmentValueId) REFERENCES equipment_value(equipmentValueId);
ALTER TABLE equipment DROP FOREIGN KEY equipment_ibfk_3;
ALTER TABLE equipment DROP COLUMN statusId;
ALTER TABLE equipment_status ADD COLUMN equipmentId INT;

ALTER TABLE equipment_status
    ADD CONSTRAINT fk_equipment_status_equipment
    FOREIGN KEY (equipmentId)
    REFERENCES equipment(equipmentId)
    ON DELETE CASCADE;
    
ALTER TABLE equipment_status
DROP FOREIGN KEY fk_equipment_status_equipment;

ALTER TABLE equipment_status
DROP COLUMN equipmentId;

ALTER TABLE equipment_status
ADD COLUMN equipmentTypeId INT;

ALTER TABLE equipment_status
ADD CONSTRAINT fk_equipment_status_type
FOREIGN KEY (equipmentTypeId)
REFERENCES equipment_type(equipmentTypeId)
ON DELETE CASCADE;


