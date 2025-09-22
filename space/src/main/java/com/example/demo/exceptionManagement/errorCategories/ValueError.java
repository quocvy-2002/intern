package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ValueError implements ErrorCodeInterface {
    // 300 -> 399
    VALUE_NOT_FOUND(300, "Value not found", HttpStatus.NOT_FOUND),
    VALUE_ALREADY_EXIST(301, "Value already exists", HttpStatus.BAD_GATEWAY);

    int code;
    String message;
    HttpStatusCode statusCode;

    ValueError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
