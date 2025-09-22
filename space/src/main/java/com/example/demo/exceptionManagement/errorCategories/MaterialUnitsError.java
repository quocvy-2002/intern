package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public enum MaterialUnitsError implements ErrorCodeInterface {
    // 400 -> 499
    MATERIAL_UNITS_NOT_FOUND(400, "Material Units not found", HttpStatus.NOT_FOUND);

    int code;
    String message;
    HttpStatusCode statusCode;

    MaterialUnitsError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}