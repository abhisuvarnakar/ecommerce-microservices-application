package com.cs.ecommerce.paymentservice.exceptions;

public class PaymentMethodNotFoundException extends PaymentException {

    public PaymentMethodNotFoundException(Long methodId) {
        super("Payment method not found with id: " + methodId);
    }

}
