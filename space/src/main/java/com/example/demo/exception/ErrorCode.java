package com.example.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    // ========== 1xxx - Common errors ==========
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(1002, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1003, "Invalid request", HttpStatus.BAD_REQUEST),

    // ========== 2xxx - Equipment errors ==========
    EQUIPMENT_NOT_FOUND(2001, "Equipment not found", HttpStatus.NOT_FOUND),
    EQUIPMENT_VALUE_NOT_FOUND(2002, "Equipment value not found", HttpStatus.NOT_FOUND),
    INVALID_EQUIPMENT_VALUE_ID(2003, "Invalid equipment value ID", HttpStatus.BAD_REQUEST),
    EQUIPMENT_ALREADY_IN_THIS_STATUS(2004, "Equipment already in this status", HttpStatus.BAD_REQUEST),
    EQUIPMENT_STATUS_NOT_FOUND(2005, "Equipment status not found", HttpStatus.NOT_FOUND),
    EQUIPMENT_STATUS_EXISTED(2006, "Equipment status has existed", HttpStatus.CONFLICT),
    INVALID_POWER_CONSUMPTION(2007, "Invalid power consumption value", HttpStatus.BAD_REQUEST),
    EQUIPMENT_VALUE_ALREADY_EXISTS(2008, "Equipment Value has existed", HttpStatus.CONFLICT),
    EQUIPMENT_STATUS_IS_REQUIRED(2010, " is required", HttpStatus.CONFLICT),
    EQUIPMENT_IS_REQUIRED(2011, " is required", HttpStatus.NOT_FOUND),

    // ========== 3xxx - Equipment type & provider ==========
    EQUIPMENT_TYPE_EXISTS(3001, "Equipment Type has existed", HttpStatus.CONFLICT),
    EQUIPMENT_TYPE_NOT_EXISTS(3002, "Equipment Type not existed", HttpStatus.NOT_FOUND),
    EQUIPMENT_TYPE_IS_REQUIRED(3004, " is required", HttpStatus.NOT_FOUND),
    PROVIDER_ALREADY_EXISTS(3003, "Provider has existed", HttpStatus.CONFLICT),
    PROVIDER_IS_REQUIRED(3005, " is required", HttpStatus.CONFLICT),

    // ========== 4xxx - Space-related errors ==========
    SPACE_NOT_FOUND(4001, "Space not found", HttpStatus.NOT_FOUND),
    SPACE_IS_REQUIRED(4002, " is required", HttpStatus.NOT_FOUND),
    SPACE_TYPE_IS_REQUIRED(4003, " is required", HttpStatus.NOT_FOUND),
    SPACE_TYPE_NOT_FOUND(4004, "Space type not found", HttpStatus.NOT_FOUND),
    SPACE_PARENT_NOT_FOUND(4005, "Space parent not found", HttpStatus.NOT_FOUND),

    // ========== 5xxx - Device errors ==========
    DEVIC_ID_NOT_NULL(5001, "Device ID cannot be empty or null", HttpStatus.BAD_REQUEST),

    // ========== 6xxx - Usage History ==========
    NO_ACTIVE_USAGE_HISTORY(6001, "No active usage history found", HttpStatus.NOT_FOUND),

    // ========== 7xxx - Status errors ==========
    INVALID_STATUS(7001, "Invalid status value", HttpStatus.BAD_REQUEST);

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
