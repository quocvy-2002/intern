package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SubSpaceTypeError implements ErrorCodeInterface {
    // 11300 -> 11399
    SUB_SPACE_TYPE_NOT_FOUND(11300, "Sub space type not found", HttpStatus.NOT_FOUND),
    SUB_SPACE_TYPE_ALREADY_EXIST(11301, "Sub space name already exist", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;

    SubSpaceTypeError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
