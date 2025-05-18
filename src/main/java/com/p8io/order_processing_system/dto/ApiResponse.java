package com.p8io.order_processing_system.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

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

    public ApiResponse(String status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
    public static <T> ApiResponse<T> success(T data, String message, int httpStatusCode) {
        return new ApiResponse<>("SUCCESS", httpStatusCode, message, data);
    }

    public static <T> ApiResponse<T> success(String message, int httpStatusCode) {
        return new ApiResponse<>("SUCCESS", httpStatusCode, message, null);
    }

    public static <T> ApiResponse<T> failure(String message, int httpStatusCode) {
        return new ApiResponse<>("FAILURE", httpStatusCode, message, null);
    }

    public static <T> ApiResponse<T> failure(String message, int httpStatusCode, T errorData) {
        return new ApiResponse<>("FAILURE", httpStatusCode, message, errorData);
    }

    public static <T> ApiResponse<T> error(String message, int httpStatusCode) {
        return new ApiResponse<>("ERROR", httpStatusCode, message, null);
    }



}
