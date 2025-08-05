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

ALTER TABLE equipment_status_log
DROP COLUMN status;

ALTER TABLE equipment_status_log
ADD COLUMN status INT;

ALTER TABLE equipment_status_log
ADD CONSTRAINT fk_status_log_status
FOREIGN KEY (status) REFERENCES equipment_status(statusId)
ON DELETE CASCADE;

SELECT * FROM uhoo_device;
SELECT * FROM space;
SELECT * FROM equipment;
SELECT * FROM equipment_type;
SELECT * FROM equipment_status_log;
SELECT * FROM equipment_usage_history;
SELECT * FROM equipment_status;
SELECT * FROM provider;
SELECT * FROM equipment_value;

CREATE TABLE device_status_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    value TEXT,
    timestamp BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE uhoo_device (
    id SERIAL PRIMARY KEY,
    device_id VARCHAR(100) UNIQUE NOT NULL,      
    name VARCHAR(255),
    location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE uhoo_measurement_data (
    id SERIAL PRIMARY KEY,
    device_id VARCHAR(100) NOT NULL,          
    timestamp TIMESTAMP NOT NULL,             
    temperature DECIMAL(5,2),
    humidity DECIMAL(5,2),
    pm2_5 DECIMAL(6,2),
    co2 DECIMAL(6,2),
    tvoc DECIMAL(6,2),
    air_pressure DECIMAL(6,2),
    ozone DECIMAL(6,2),
    carbon_monoxide DECIMAL(6,2),
    nitrogen_dioxide DECIMAL(6,2),
    noise DECIMAL(6,2),
    light DECIMAL(6,2),
    FOREIGN KEY (device_id) REFERENCES uhoo_device(device_id)
);
INSERT INTO uhoo_device (device_id, name, location, created_at)
VALUES ('b0b11342a3cf', 'Industry 4.0 Innovation Center', 'Office Space', CURRENT_TIMESTAMP);



