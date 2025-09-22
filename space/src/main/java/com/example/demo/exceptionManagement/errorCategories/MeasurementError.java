package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MeasurementError implements ErrorCodeInterface {
    // 1300 -> 1399
    MEASUREMENT_NOT_FOUND(1300, "Measurement not found", HttpStatus.NOT_FOUND),
    MEASUREMENT_ALREADY_EXIST(1301, "Measurement already exist", HttpStatus.BAD_REQUEST),
    MEASUREMENT_CREATION_FAILED(10402, "Failed to create measurement", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatusCode statusCode;

    MeasurementError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}