package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum AaoScheduleError implements ErrorCodeInterface {
    // 600 -> 699
    AAO_SCHEDULE_NOT_FOUND(600, "Aao Schedule not found", HttpStatus.NOT_FOUND);

    int code;
    String message;
    HttpStatusCode statusCode;

    AaoScheduleError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
