package com.p8io.order_processing_system.exceptions;

import com.p8io.order_processing_system.dto.ApiResponse;
import com.p8io.order_processing_system.util.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OrderAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleOrderAlreadyExistsException(OrderAlreadyExistsException ex) {
        log.info("handle OrderAlready exists exception");
        ApiResponse<Object> response = new ApiResponse<>(
                ApplicationConstants.FAILURE,
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        log.info(response.toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleOrderNotFoundException(OrderNotFoundException ex) {
        log.info("handle OrderNot found exception");
        ApiResponse<Object> response = new ApiResponse<>(
                ApplicationConstants.FAILURE,
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        log.info(response.toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        ApiResponse<Object> response = new ApiResponse<>(ApplicationConstants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
