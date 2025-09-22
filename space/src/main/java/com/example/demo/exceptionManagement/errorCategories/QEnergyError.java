package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public enum QEnergyError implements ErrorCodeInterface {
    // 500 -> 599
    QENERGY_NOT_FOUND(500, "QEnergy not found", HttpStatus.NOT_FOUND),
    NO_QENERGY_AT_SPECIFIC_DATE_RANGE(501, "This date range does not have data", HttpStatus.BAD_REQUEST),
    INVALID_DATE_TYPE(502, "Invalid Date Type. Only supports: DAY, WEEK, MONTH, YEAR", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode statusCode;

    QEnergyError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}