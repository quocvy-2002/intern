package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ZoneError implements ErrorCodeInterface {
    // 11200 -> 11299
    ZONE_ALREADY_EXISTS(11000, "Zone already exists", HttpStatus.CONFLICT),
    ZONE_NOT_FOUND(11001, "Zone not found", HttpStatus.NOT_FOUND),
    INVALID_WKT_FORMAT(11002, "Invalid WKT format", HttpStatus.BAD_REQUEST),
    DUPLICATE_ZONE_NAMES_IN_REQUEST(11003, "Duplicate zone names in request", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;

    ZoneError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
