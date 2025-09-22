package com.example.demo.exceptionManagement.exceptions;

import com.example.SmartBuildingBackend.exceptionManagement.ErrorCode;
import com.example.SmartBuildingBackend.exceptionManagement.ErrorCodeInterface;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AppException extends RuntimeException {
    ErrorCode errorCode;
    Map<String, Object> context;

    public AppException(ErrorCodeInterface errorCodeInterface) {
        this(new ErrorCode(errorCodeInterface.getCode(), errorCodeInterface.getMessage(), 
                          errorCodeInterface.getStatusCode()));
    }

    public AppException(ErrorCode errorCode) {
        this(errorCode, Collections.emptyMap());
    }

    public AppException(ErrorCode errorCode, Map<String, Object> context) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.context = context;
    }

    public AppException(ErrorCodeInterface errorCodeInterface, Map<String, Object> context) {
        this(new ErrorCode(errorCodeInterface.getCode(), errorCodeInterface.getMessage(), 
                          errorCodeInterface.getStatusCode()), context);
    }
}
