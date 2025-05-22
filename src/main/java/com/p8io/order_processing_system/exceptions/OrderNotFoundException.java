package com.p8io.order_processing_system.exceptions;

public class OrderNotFoundException extends RuntimeException{
   public OrderNotFoundException(String message){
        super(message);
    }
}
