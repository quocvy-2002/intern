package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum TreeError implements ErrorCodeInterface {
    // 11100 -> 11199
    TREE_ALREADY_EXISTS(11100, "Tree with this location already exists", HttpStatus.CONFLICT),
    TREE_NOT_FOUND(11101, "Tree not found", HttpStatus.NOT_FOUND),
    TREE_CREATION_FAILED(11102, "Failed to create tree", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatusCode statusCode;

    TreeError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}