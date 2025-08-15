package com.cs.ecommerce.sharedmodules.exceptions;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long productId) {
        super("Product not found: " + productId);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }

}