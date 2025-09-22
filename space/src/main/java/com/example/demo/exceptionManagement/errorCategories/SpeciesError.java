package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public enum SpeciesError implements ErrorCodeInterface {
    // 11200 -> 11299
    SPECIES_ALREADY_EXISTS(11200, "Species already exists", HttpStatus.CONFLICT),
    SPECIES_NOT_FOUND(11201, "Species not found", HttpStatus.NOT_FOUND);

    int code;
    String message;
    HttpStatusCode statusCode;

    SpeciesError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}