package com.cs.ecommerce.orderservice.exceptions;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException (String message) {
        super(message);
    }

}
