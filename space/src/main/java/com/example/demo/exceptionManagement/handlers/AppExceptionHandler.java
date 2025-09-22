package com.example.demo.exceptionManagement.handlers;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCode;
import com.example.SmartBuildingBackend.exceptionManagement.exceptions.AppException;
import com.example.SmartBuildingBackend.model.dto.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AppExceptionHandler implements ExceptionHandlerInterface<AppException> {
    
    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof AppException;
    }
    
    @Override
    public ResponseEntity<ApiResponse<?>> handle(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        
        ApiResponse.ApiResponseBuilder<Object> responseBuilder = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage());
        
        if (!exception.getContext().isEmpty()) {
            responseBuilder.result(exception.getContext());
        }
        
        return ResponseEntity.status(errorCode.getStatusCode()).body(responseBuilder.build());
    }
}