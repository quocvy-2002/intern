package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public enum PosterError implements ErrorCodeInterface {
    // 1700 -> 1799
    POSTER_NOT_FOUND(1700, "Poster not found", HttpStatus.NOT_FOUND);

    int code;
    String message;
    HttpStatusCode statusCode;

    PosterError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}