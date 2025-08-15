package com.cs.ecommerce.sharedmodules.exceptions;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(Long productId, int available, int requested) {
        super(String.format("Insufficient stock for product %d. Available: %d, Requested: %d",
                productId, available, requested));
    }

}
