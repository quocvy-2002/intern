package com.example.demo.exceptionManagement;

import com.example.SmartBuildingBackend.exceptionManagement.errorCategories.SystemError;
import com.example.SmartBuildingBackend.exceptionManagement.errorCategories.ValidationError;
import com.example.SmartBuildingBackend.exceptionManagement.exceptions.AppException;
import com.example.SmartBuildingBackend.exceptionManagement.exceptions.ValidationException;
import com.example.SmartBuildingBackend.exceptionManagement.handlers.AppExceptionHandler;
import com.example.SmartBuildingBackend.exceptionManagement.handlers.ExceptionHandlerInterface;
import com.example.SmartBuildingBackend.exceptionManagement.handlers.ValidationExceptionHandler;
import com.example.SmartBuildingBackend.model.dto.api.ApiResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalExceptionHandler {

    List<ExceptionHandlerInterface<? extends Exception>> exceptionHandlers;

    @Autowired
    public GlobalExceptionHandler(List<ExceptionHandlerInterface<? extends Exception>> exceptionHandlers) {
        this.exceptionHandlers = exceptionHandlers;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception exception) {

        for (ExceptionHandlerInterface<? extends Exception> handler : exceptionHandlers) {
            if (handler.canHandle(exception)) {
                return ((ExceptionHandlerInterface<Exception>) handler).handle(exception);
            }
        }

        return handleGenericException(exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ValidationException.FieldValidationError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        ValidationException validationException = new ValidationException(fieldErrors);
        return new ValidationExceptionHandler().handle(validationException);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException exception) {
        AppException appException = new AppException(SystemError.UNAUTHORIZED);
        return new AppExceptionHandler().handle(appException);
    }

    private ValidationException.FieldValidationError mapFieldError(FieldError fieldError) {
        String errorKey = fieldError.getDefaultMessage();
        String fieldName = fieldError.getField();
        String capitalizedFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        ErrorCode errorCode = findErrorCodeByKey(errorKey);
        String finalMessage = errorCode.getMessage().replace("This field", capitalizedFieldName);

        return new ValidationException.FieldValidationError(fieldName, errorKey, finalMessage);
    }

    private ErrorCode findErrorCodeByKey(String errorKey) {
        try {
            ValidationError validationError = ValidationError
                    .valueOf(errorKey);
            return new ErrorCode(validationError.getCode(), validationError.getMessage(),
                    validationError.getStatusCode());
        } catch (IllegalArgumentException e) {
            return new ErrorCode(ValidationError.KEY_INVALID.getCode(),
                    ValidationError.KEY_INVALID.getMessage(),
                    ValidationError.KEY_INVALID.getStatusCode());
        }
    }

    private ResponseEntity<ApiResponse<?>> handleGenericException(Exception exception) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(500)
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}