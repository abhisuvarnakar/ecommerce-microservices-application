package com.cs.ecommerce.orderservice.exceptions;

public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(Long itemId) {
        super("Cart item not found: " + itemId);
    }

}
