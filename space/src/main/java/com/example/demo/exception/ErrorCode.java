package com.example.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(1002, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    SPACE_NOT_FOUND(1003, "Space not found", HttpStatus.NOT_FOUND),
    SPACE_TYPE_NOT_FOUND(1004,"SPACE_TYPE_NOT_FOUND", HttpStatus.NOT_FOUND),
    SPACE_PARENT_NOT_FOUND(1005,"SPACE_PARENT_NOT_FOUND", HttpStatus.NOT_FOUND),
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
