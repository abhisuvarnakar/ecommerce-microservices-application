package com.cs.ecommerce.paymentservice.advices;

import com.cs.ecommerce.paymentservice.exceptions.PaymentMethodNotFoundException;
import com.cs.ecommerce.paymentservice.exceptions.PaymentNotFoundException;
import com.cs.ecommerce.paymentservice.exceptions.PaymentProcessingException;
import com.cs.ecommerce.paymentservice.exceptions.RefundProcessingException;
import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.sharedmodules.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handlePaymentNotFoundException(PaymentNotFoundException ex) {
        log.error("Payment not found error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(PaymentMethodNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handlePaymentMethodNotFoundException(PaymentMethodNotFoundException ex) {
        log.error("Payment method not found error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler({PaymentProcessingException.class, RefundProcessingException.class})
    public ResponseEntity<ApiResponse<String>> handlePaymentProcessingException(RuntimeException ex) {
        log.error("Payment processing error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred"));
    }


}
