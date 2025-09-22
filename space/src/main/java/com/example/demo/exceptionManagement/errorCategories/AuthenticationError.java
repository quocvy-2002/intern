package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum AuthenticationError implements ErrorCodeInterface {
    // 1500 -> 1599
    LOGIN_FAILED(1500, "Wrong email or password", HttpStatus.BAD_REQUEST),
    USER_DELETED(1501, "User has been disable", HttpStatus.BAD_REQUEST),
    USER_BLOCKED(1502, "User has been blocked", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1503, "User not found", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    AuthenticationError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}