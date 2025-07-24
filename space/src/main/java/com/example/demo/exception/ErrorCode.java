package com.example.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    EQUIPMENT_VALUE_NOT_FOUND(1001, "Equipment value not found", HttpStatus.NOT_FOUND),
    INVALID_EQUIPMENT_VALUE_ID(1003, "Invalid equipment value ID", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(1002, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    SPACE_NOT_FOUND(1003, "Space not found", HttpStatus.NOT_FOUND),
    SPACE_TYPE_NOT_FOUND(1004,"SPACE_TYPE_NOT_FOUND", HttpStatus.NOT_FOUND),
    EQUIPMENT_ALREADY_IN_THIS_STATUS(1006, "Equipment already in this status", HttpStatus.BAD_REQUEST),
    SPACE_PARENT_NOT_FOUND(1005,"SPACE_PARENT_NOT_FOUND", HttpStatus.NOT_FOUND),
    EQUIPMENT_NOT_FOUND(1003, "Equipment not found", HttpStatus.NOT_FOUND),
    EQUIPMENT_STATUS_NOT_FOUND(1003, "Equipment not found", HttpStatus.NOT_FOUND),
    EQUIPMENT_STATUS_EXISTED(1003, "Equipment Status has existed", HttpStatus.NOT_FOUND),
    INVALID_POWER_CONSUMPTION(1004, "Invalid power consumption value", HttpStatus.BAD_REQUEST),
    PROVIDER_ALREADY_EXISTS(1002, "Provider has existed", HttpStatus.CONFLICT),
    EQUIPMENT_TYPE_EXISTS(1002, "Equipment Type has existed", HttpStatus.CONFLICT),
    EQUIPMENT_TYPE_NOT_EXISTS(1002, "Equipment Type  not existed", HttpStatus.CONFLICT),
    EQUIPMENT_VALUE_ALREADY_EXISTS(1002, "Equipment Value has existed", HttpStatus.CONFLICT),
    NO_ACTIVE_USAGE_HISTORY(1004, "No active usage history found", HttpStatus.NOT_FOUND),
    INVALID_STATUS(1005, "Invalid status value", HttpStatus.BAD_REQUEST);

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
