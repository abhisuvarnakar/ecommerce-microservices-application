package com.cs.ecommerce.orderservice.exceptions;

import com.cs.ecommerce.orderservice.enums.OrderStatus;

public class OrderCancellationException extends RuntimeException {

    public OrderCancellationException(Long orderId, OrderStatus currentStatus) {
        super(String.format("Order %d cannot be cancelled in its current status: %s",
                orderId, currentStatus));
    }

}
