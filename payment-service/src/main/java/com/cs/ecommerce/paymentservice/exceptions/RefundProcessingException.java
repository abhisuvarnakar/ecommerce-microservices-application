package com.cs.ecommerce.paymentservice.exceptions;

public class RefundProcessingException extends PaymentException {

    public RefundProcessingException(String message) {
        super(message);
    }
    public RefundProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}
