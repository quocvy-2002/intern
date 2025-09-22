package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public enum MaterialMeasurementError implements ErrorCodeInterface {
    // 1400 -> 1499
    MATERIAL_MEASUREMENT_NOT_FOUND(1400, "Material Measurement not found", HttpStatus.NOT_FOUND),
    MATERIAL_MEASUREMENT_ALREADY_EXIST(1401, "Material Measurement already exist", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;

    MaterialMeasurementError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}