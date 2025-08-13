package com.cs.ecommerce.orderservice.exceptions;

public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException (String message) {
        super(message);
    }

}
