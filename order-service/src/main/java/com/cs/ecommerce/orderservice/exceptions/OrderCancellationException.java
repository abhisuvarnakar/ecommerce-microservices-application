package com.cs.ecommerce.orderservice.exceptions;

public class OrderCancellationException extends RuntimeException {

    public OrderCancellationException (String message) {
        super(message);
    }

}
