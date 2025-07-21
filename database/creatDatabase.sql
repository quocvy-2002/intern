CREATE DATABASE space;
use space;
CREATE TABLE space (
	spaceId VARCHAR(10) PRIMARY KEY,
    spaceName VARCHAR(255) NOT NULL,
    spaceTypeName VARCHAR(255) NOT NULL,
    spaceTypeLevel VARCHAR(255) NOT NULL,
    parentId VARCHAR(10),
    FOREIGN KEY (spaceTypeName, spaceTypeLevel) REFERENCES spaceType(spaceTypeName, spaceTypeLevel)
);
ALTER TABLE space
MODIFY COLUMN parentId INT;
ALTER TABLE space
MODIFY COLUMN spaceId INT;
ALTER TABLE SPACE MODIFY COLUMN spaceId INT NOT NULL AUTO_INCREMENT;

CREATE TABLE spaceType(
	 spaceTypeName VARCHAR(255) NOT NULL,
    spaceTypeLevel VARCHAR(255) NOT NULL,
     PRIMARY KEY (spaceTypeName, spaceTypeLevel)
);
INSERT INTO spaceType (spaceTypeName, spaceTypeLevel)
VALUES ('ROOM', '2');
INSERT INTO spaceType (spaceTypeName, spaceTypeLevel)
VALUES ('SPACE', 'null');
INSERT INTO spaceType (spaceTypeName, spaceTypeLevel)
VALUES ('FLOOl', '1');
DELETE FROM spaceType;
SELECT * FROM spaceType;