package com.p8io.order_processing_system.exceptions;

public class OrderAlreadyExistsException extends RuntimeException{
    public OrderAlreadyExistsException(String message){
        super(message);
    }
}
