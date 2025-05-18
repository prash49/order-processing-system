package com.p8io.order_processing_system.exceptions;

import com.p8io.order_processing_system.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

   /* @ExceptionHandler(OrderAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleOrderAlreadyExistsExcepton(OrderAlreadyExistsException orderAlreadyExistsException)
    {

    }*/
}
