package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ValidationError implements ErrorCodeInterface {
    // 9000 -> 9999
    INVALID_FIELDS(9000, "Invalid fields", HttpStatus.BAD_REQUEST),
    FIELD_REQUIRED(9001, "This field is required", HttpStatus.BAD_REQUEST),
    FIELD_POSITIVE(9002, "This field must be positive", HttpStatus.BAD_REQUEST),
    EMAIL_FORMAT(9003, "Wrong email format", HttpStatus.BAD_REQUEST),
    FIELD_POSITIVE_AND_ZERO(9004, "This field must be equal or greater than zero", HttpStatus.BAD_REQUEST),
    DATE_INVALID(9005, "Invalid date", HttpStatus.BAD_REQUEST),
    FAILED_TO_GET_NEW_DEVICE(9006, "Failed to get new device list", HttpStatus.BAD_REQUEST),
    INVALID_DBH(9007, "DBH must be positive", HttpStatus.BAD_REQUEST),
    INVALID_HEIGHT(9008, "Height must be positive", HttpStatus.BAD_REQUEST),
    INVALID_CANOPY_DIAMETER(9009, "Canopy diameter must be positive", HttpStatus.BAD_REQUEST),
    EXCEED_AVAILABLE_UNITS(9010, "Transport quantity exceeds available units", HttpStatus.BAD_REQUEST),
    NEGATIVE_VALUE_AFTER_UPDATING(9011, "Negative value after updating", HttpStatus.BAD_REQUEST),
    LEVEL_INVALID_HIERARCHY(9021, "Invalid level for hierarchy", HttpStatus.BAD_REQUEST),
    NOT_FOR_WAREHOUSE(9022, "Warehouse is not be used in this function", HttpStatus.BAD_REQUEST),
    ONLY_FOR_FLOOR(9023, "This function is only for Floor", HttpStatus.BAD_REQUEST),
    EMPTY_REQUEST_LIST(9024, "Request list is empty", HttpStatus.BAD_REQUEST),
    BATCH_VALIDATION_FAILED(9025, "Batch validation failed", HttpStatus.BAD_REQUEST),
    EMPTY_FILE(9026, "Uploaded file is empty", HttpStatus.BAD_REQUEST),
    EMPTY_DATA_FILE(9027, "No valid data found in file", HttpStatus.BAD_REQUEST),
    INVALID_DATE_RANGE(9028, "Invalid date range provided", HttpStatus.BAD_REQUEST),
    EXPORT_FAILED(9029, "Failed to export tree data", HttpStatus.INTERNAL_SERVER_ERROR),
    COORDINATES_OUT_OF_RANGE(9030, "Coordinates are out of valid range", HttpStatus.BAD_REQUEST),
    INVALID_PLANTED_DATE(9031, "Invalid planted date for tree", HttpStatus.BAD_REQUEST),
    COORDINATES_REQUIRED(9032, "Coordinates are required", HttpStatus.BAD_REQUEST),
    INVALID_GIRTH(9033, "Girth must be positive", HttpStatus.BAD_REQUEST),
    INVALID_HEALTH_STATUS(9034, "Invalid health status", HttpStatus.BAD_REQUEST),
    KEY_INVALID(9999, "Invalid message key", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;

    ValidationError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}