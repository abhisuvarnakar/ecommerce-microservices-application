package com.cs.ecommerce.orderservice.exceptions;

public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException(Long userId) {
        super("Cart not found for user: " + userId);
    }

}
