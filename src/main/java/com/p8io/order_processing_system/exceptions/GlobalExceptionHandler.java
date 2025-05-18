package com.p8io.order_processing_system.exceptions;

import com.p8io.order_processing_system.dto.ApiResponse;
import com.p8io.order_processing_system.util.AppUtil;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleOrderAlreadyExistsExcepton(OrderAlreadyExistsException orderAlreadyExistsException) {
        ApiResponse<Object> response = new ApiResponse<>(AppUtil.FAILURE, HttpStatus.BAD_REQUEST.value(), orderAlreadyExistsException.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors()
                .forEach(error -> {
                            String filedName = (error instanceof FieldError) ? ((FieldError) error).getField() : error.getObjectName();
                            String errorMessage = error.getDefaultMessage();
                            errors.put(filedName, errorMessage);
                        }
                );
        ApiResponse<Map<String, String>> response = new ApiResponse<>(AppUtil.ERROR, HttpStatus.BAD_REQUEST.value(), errors.toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
