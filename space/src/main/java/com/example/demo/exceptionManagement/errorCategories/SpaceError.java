package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SpaceError implements ErrorCodeInterface {
    // 1 -> 99
    SPACE_NOT_FOUND(1, "Space not found", HttpStatus.NOT_FOUND),
    SPACE_ALREADY_HAS_WAREHOUSE(2, "Space already has warehouse", HttpStatus.BAD_REQUEST),
    NOT_FOR_WAREHOUSE(3, "Warehouse space is not be used in this function", HttpStatus.BAD_REQUEST),
    ONLY_FOR_WAREHOUSE(4, "This function is only for warehouse space", HttpStatus.BAD_REQUEST),
    SITE_ID_NOT_FOUND(5, "Site ID not found", HttpStatus.NOT_FOUND),
    ONLY_FOR_BLOCK(6, "This function is only for Block type", HttpStatus.BAD_REQUEST),
    SPACE_PARENT_NOT_FOUND(7, "Space parent not found", HttpStatus.NOT_FOUND),
    SPACE_NAME_ALREADY_EXIST(10, "This space name already exist", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;

    SpaceError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}