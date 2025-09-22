package com.example.demo.exceptionManagement;

import org.springframework.http.HttpStatusCode;

public interface ErrorCodeInterface {
    int getCode();

    String getMessage();

    HttpStatusCode getStatusCode();
}