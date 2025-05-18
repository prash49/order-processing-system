package com.p8io.order_processing_system.dto;

import org.springframework.http.HttpStatus;

public class ApiResponse<T> {
    private String status;  // "SUCCESS", "FAILURE", "ERROR"
    private int code;       // HTTP status code (200, 400, 500, etc.)
    private String message;
    private T  data;

    public ApiResponse(String status, int code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }




}
