package com.cs.ecommerce.paymentservice.exceptions;

public class PaymentNotFoundException extends PaymentException {

    public PaymentNotFoundException(Long paymentId) {
        super("Payment not found with id: " + paymentId);
    }

}
