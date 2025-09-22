package com.example.demo.exceptionManagement.errorCategories;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

// 10000 -> 10999

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SystemError implements ErrorCodeInterface {
    UNAUTHORIZED(10000, "You do not have permission", HttpStatus.FORBIDDEN),
    FAILED_TO_GET_ACCESS_TOKEN(10001, "Failed to get access token", HttpStatus.INTERNAL_SERVER_ERROR),
    FAILED_TO_GET_LIVE_POWER(10002, "Failed to get live power", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(10003, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    FAILED_TO_REFRESH(10004, "Failed to refresh token", HttpStatus.EXPECTATION_FAILED),
    SESSION_INVALID(10005, "Your session is invalid, please login again!", HttpStatus.UNAUTHORIZED),
    ENCRYPT_FAILED(10006, "Encrypted failed", HttpStatus.INTERNAL_SERVER_ERROR),
    IMPORT_FAILED(10010, "Import failed", HttpStatus.INTERNAL_SERVER_ERROR),
    EXPORT_FAILED(10011, "Export failed", HttpStatus.INTERNAL_SERVER_ERROR),
    EMPTY_IMPORT_DATA(10012, "Import data is empty", HttpStatus.BAD_REQUEST),
    EMPTY_REQUEST_LIST(10013, "Request list is empty", HttpStatus.BAD_REQUEST),
    EMPTY_FILE(10014, "File is empty", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(10015, "Invalid file type", HttpStatus.BAD_REQUEST),
    EMPTY_DATA_FILE(10016, "No data found in file", HttpStatus.BAD_REQUEST),
    FILE_PROCESSING_ERROR(10017, "Error processing file", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_EXCEL_FILE(10018, "Invalid Excel file format", HttpStatus.BAD_REQUEST),
    BATCH_SAVE_FAILED(10601, "Failed to save batch", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatusCode statusCode;

    SystemError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
