package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum LogValueError implements ErrorCodeInterface {
    // 1100 -> 1199
    LOG_VALUE_NOT_FOUND(900, "Log value not found", HttpStatus.NOT_FOUND),
    LOG_VALUE_ALREADY_EXIST(901, "This value name already exist", HttpStatus.BAD_REQUEST),
    MAPPING_UPDATE_FAILED(902, "Failed to update value mapping", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatusCode statusCode;

    LogValueError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}