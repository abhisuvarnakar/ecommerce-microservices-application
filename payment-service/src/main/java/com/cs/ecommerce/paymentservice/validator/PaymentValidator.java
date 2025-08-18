package com.cs.ecommerce.paymentservice.validator;

import com.cs.ecommerce.paymentservice.clients.OrderServiceClient;
import com.cs.ecommerce.paymentservice.dto.PaymentMethodRequestDTO;
import com.cs.ecommerce.sharedmodules.dto.payment.RefundRequestDTO;
import com.cs.ecommerce.paymentservice.entities.Payment;
import com.cs.ecommerce.sharedmodules.enums.payment.PaymentStatus;
import com.cs.ecommerce.paymentservice.exceptions.PaymentProcessingException;
import com.cs.ecommerce.paymentservice.exceptions.RefundProcessingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentValidator {

    private final OrderServiceClient orderServiceClient;
    private final ObjectMapper objectMapper;

    /*public void validatePaymentRequest(PaymentRequestDTO paymentRequest) {
        if (paymentRequest.getPaymentMethod() == PaymentMethod.CREDIT_CARD ||
                paymentRequest.getPaymentMethod() == PaymentMethod.DEBIT_CARD) {
            if (paymentRequest.getCardDetails() == null) {
                throw new PaymentProcessingException("Card details are required for card payments");
            }
        }

        // TODO: Add validation for other payment methods if needed
    }*/

    public void validateOrder(Long orderId, Long userId) {
        String json = orderServiceClient.getOrderDetails(orderId, userId);
        try {
            JsonNode node = objectMapper.readTree(json);
            boolean exists = node.path("data").isObject()
                    && !node.path("data").isEmpty();
            if (!exists) {
                throw new PaymentProcessingException("Order not found or doesn't belong to user");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void validateRefundRequest(Payment payment, RefundRequestDTO refundRequest) {
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RefundProcessingException("Only completed payments can be refunded");
        }

        if (refundRequest.getAmount().compareTo(payment.getAmount()) > 0) {
            throw new RefundProcessingException("Refund amount cannot exceed payment amount");
        }

        // TODO: Add more business validations as needed
    }

    public void validatePaymentMethodRequest(PaymentMethodRequestDTO request) {
        switch (request.getPaymentMethod()) {
            case CREDIT_CARD:
            case DEBIT_CARD:
                if (request.getCardDetails() == null) {
                    throw new PaymentProcessingException("Card details are required for card " +
                            "payments");
                }
                break;

            case UPI:
                if (request.getUpiId() == null || request.getUpiId().isBlank()) {
                    throw new PaymentProcessingException("UPI ID is required");
                }
                break;

            case BANK_TRANSFER:
                if (request.getAccountNumber() == null || request.getAccountNumber().isBlank()
                        || request.getIfscCode() == null || request.getIfscCode().isBlank()) {
                    throw new PaymentProcessingException("Account number and IFSC code are " +
                            "required");
                }
                break;

            case CASH_ON_DELIVERY:
                break;
        }
    }
}
