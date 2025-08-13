package com.cs.ecommerce.orderservice.exceptions;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException (String message) {
        super(message);
    }

}
