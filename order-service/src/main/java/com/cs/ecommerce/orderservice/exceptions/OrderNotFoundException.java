package com.cs.ecommerce.orderservice.exceptions;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException (String message) {
        super(message);
    }
}
