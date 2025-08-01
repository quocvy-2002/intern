package com.example.demo.exception;

import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.response.FieldErrorDTO;
import com.example.demo.model.dto.response.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handler cho Exception chung
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(Exception exception) {
        log.error("Exception: ", exception);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    // Handler duy nhất cho MethodArgumentNotValidException (VALIDATION ERROR)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldErrorDTO> errorList = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    String field = error.getField();
                    String codeName = error.getDefaultMessage(); // "IS_REQUIRED"
                    ErrorCode errorCode = ErrorCode.fromCode(codeName); // lấy Enum tương ứng

                    return FieldErrorDTO.builder()
                            .field(field)
                            .message(field + errorCode.getMessage()) // "spaceName is required"
                            .build();
                })
                .collect(Collectors.toList());

        int errorCode = errorList.isEmpty() ? 3000 : ErrorCode.fromCode("IS_REQUIRED").getCode(); // fallback nếu lỗi không tìm thấy

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .code(errorCode)
                .errors(errorList)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    // Handler cho AppException
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }
}
