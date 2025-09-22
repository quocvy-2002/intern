package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum EquipmentProviderError implements ErrorCodeInterface {
    // 800 -> 899
    EQUIPMENT_PROVIDER_NOT_FOUND(800, "Equipment provider not found", HttpStatus.NOT_FOUND);

    int code;
    String message;
    HttpStatusCode statusCode;

    EquipmentProviderError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}