package com.example.demo.exceptionManagement.handlers;

import com.example.SmartBuildingBackend.model.dto.api.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface ExceptionHandlerInterface<T extends Exception> {
    boolean canHandle(Exception exception);

    ResponseEntity<ApiResponse<?>> handle(T exception);
}