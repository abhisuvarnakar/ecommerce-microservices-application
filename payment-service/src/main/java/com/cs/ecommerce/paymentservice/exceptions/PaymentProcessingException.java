package com.cs.ecommerce.paymentservice.exceptions;

public class PaymentProcessingException extends PaymentException {

    public PaymentProcessingException(String message) {
        super(message);
    }
    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}
