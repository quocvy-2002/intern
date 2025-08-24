package com.example.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    // ========== 1xxx - Common errors ==========
    ZONE_ALREADY_EXISTS(1009, "Zone already exists", HttpStatus.CONFLICT),
    ZONE_NOT_EXISTS(1010, "Zone not found", HttpStatus.NOT_FOUND),
    INVALID_WKT_FORMAT(1012, "Invalid WKT format", HttpStatus.BAD_REQUEST),
    DUPLICATE_ZONE_NAMES_IN_REQUEST(1013, "Duplicate zone names in request", HttpStatus.BAD_REQUEST),
    EMPTY_REQUEST_LIST(1015, "Request list is empty", HttpStatus.BAD_REQUEST),
    EMPTY_FILE(1016, "File is empty", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(1017, "Invalid file type", HttpStatus.BAD_REQUEST),
    EMPTY_DATA_FILE(1018, "No data found in file", HttpStatus.BAD_REQUEST),
    FILE_PROCESSING_ERROR(1019, "Error processing file", HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(1002, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1003, "Invalid request", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(1004, "Invalid input data", HttpStatus.BAD_REQUEST),

    // ========== 2xxx - Equipment errors ==========
    EQUIPMENT_NOT_FOUND(2001, "Equipment not found", HttpStatus.NOT_FOUND),
    EQUIPMENT_VALUE_NOT_FOUND(2002, "Equipment value not found", HttpStatus.NOT_FOUND),
    INVALID_EQUIPMENT_VALUE_ID(2003, "Invalid equipment value ID", HttpStatus.BAD_REQUEST),
    EQUIPMENT_ALREADY_IN_THIS_STATUS(2004, "Equipment already in this status", HttpStatus.BAD_REQUEST),
    EQUIPMENT_STATUS_NOT_FOUND(2005, "Equipment status not found", HttpStatus.NOT_FOUND),
    EQUIPMENT_STATUS_EXISTED(2006, "Equipment status has existed", HttpStatus.CONFLICT),
    INVALID_POWER_CONSUMPTION(2007, "Invalid power consumption value", HttpStatus.BAD_REQUEST),
    EQUIPMENT_VALUE_ALREADY_EXISTS(2008, "Equipment value has existed", HttpStatus.CONFLICT),

    // ========== 3xxx - Equipment type & provider ==========
    EQUIPMENT_TYPE_EXISTS(3001, "Equipment type has existed", HttpStatus.CONFLICT),
    EQUIPMENT_TYPE_NOT_EXISTS(3002, "Equipment type not found", HttpStatus.NOT_FOUND),
    PROVIDER_ALREADY_EXISTS(3003, "Provider has existed", HttpStatus.CONFLICT),
    IS_REQUIRED(3004, "Field is required", HttpStatus.BAD_REQUEST),

    // ========== 4xxx - Space-related errors ==========
    SPACE_NOT_FOUND(4001, "Space not found", HttpStatus.NOT_FOUND),
    SPACE_TYPE_NOT_FOUND(4004, "Space type not found", HttpStatus.NOT_FOUND),
    SPACE_PARENT_NOT_FOUND(4005, "Space parent not found", HttpStatus.NOT_FOUND),

    // ========== 5xxx - Device errors ==========
    DEVIC_ID_NOT_NULL(5001, "Device ID cannot be empty or null", HttpStatus.BAD_REQUEST),

    // ========== 6xxx - Usage History ==========
    NO_ACTIVE_USAGE_HISTORY(6001, "No active usage history found", HttpStatus.NOT_FOUND),

    // ========== 7xxx - Status errors ==========
    FAILED_TO_GET_TOKEN(7002, "Failed to get token", HttpStatus.INTERNAL_SERVER_ERROR),
    FAILED_TO_GET_LIVE_POWER(7003, "Failed to get live power", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_STATUS(7001, "Invalid status value", HttpStatus.BAD_REQUEST),

    // ========== 8xxx - Tree errors ==========
    SPECIES_ALREADY_EXISTS(8001, "Species has existed", HttpStatus.CONFLICT),
    TREE_NOT_EXISTS(8004, "Tree not found", HttpStatus.NOT_FOUND),
    SPECIES_NOT_EXISTS(8005, "Species not found", HttpStatus.NOT_FOUND),
    TREE_CREATION_FAILED(8006, "Failed to create tree", HttpStatus.INTERNAL_SERVER_ERROR),
    MEASUREMENT_CREATION_FAILED(8007, "Failed to create measurement", HttpStatus.INTERNAL_SERVER_ERROR),
    BATCH_SAVE_FAILED(8008, "Failed to save batch", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_DBH(8010, "DBH must be positive", HttpStatus.BAD_REQUEST),
    INVALID_HEIGHT(8011, "Height must be positive", HttpStatus.BAD_REQUEST),
    INVALID_CANOPY_DIAMETER(8012, "Canopy diameter must be positive", HttpStatus.BAD_REQUEST),
    INVALID_EXCEL_FILE(8009, "Invalid Excel file format", HttpStatus.BAD_REQUEST);

    // Fields
    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    // Constructor
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public static ErrorCode fromCode(String codeName) {
        try {
            return ErrorCode.valueOf(codeName);
        } catch (IllegalArgumentException e) {
            return ErrorCode.INVALID_REQUEST;
        }
    }
}
