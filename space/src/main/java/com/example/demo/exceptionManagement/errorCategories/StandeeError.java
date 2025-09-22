package com.example.demo.exceptionManagement.errorCategories;


import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum StandeeError implements ErrorCodeInterface {
    // 1600 -> 1699
    STANDEE_NOT_FOUND(1600, "Standee not found", HttpStatus.NOT_FOUND),
    STANDEE_NAME_ALREADY_EXIST(1601, "Standee with this name already exist", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;

    StandeeError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}