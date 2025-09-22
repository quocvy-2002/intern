package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MeasurementTreeError implements ErrorCodeInterface {
    // 11000 -> 11099
    MEASUREMENT_CREATION_FAILED(11000, "Failed to create measurement", HttpStatus.INTERNAL_SERVER_ERROR),
    MEASUREMENT_TREE_NOT_FOUND(11001, "Measurement tree not found", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;

    MeasurementTreeError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}