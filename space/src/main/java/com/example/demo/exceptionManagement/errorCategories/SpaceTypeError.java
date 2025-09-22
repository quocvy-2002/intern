package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SpaceTypeError implements ErrorCodeInterface {
    // 100 -> 199
    SPACE_TYPE_NOT_FOUND(100, "Type not found", HttpStatus.NOT_FOUND),
    SPACE_TYPE_LEVEL_ALREADY_EXIST(101, "This level is already exist", HttpStatus.BAD_REQUEST),
    SPACE_TYPE_NAME_ALREADY_EXIST(102, "This name is already exist", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;

    SpaceTypeError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}