package com.cs.ecommerce.paymentservice.service;

import com.cs.ecommerce.paymentservice.dto.PaymentRequestDTO;
import com.cs.ecommerce.paymentservice.dto.RefundRequestDTO;
import com.cs.ecommerce.paymentservice.entities.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;

public interface PaymentGatewayService {

    PaymentGatewayResponse processPayment(PaymentRequestDTO paymentRequest);

    PaymentGatewayResponse processRefund(Payment payment, RefundRequestDTO refundRequest);

    @Data
    @AllArgsConstructor
    public static class PaymentGatewayResponse {
        private String transactionId;
        private String status;
        private String gatewayName;
        private String rawResponse;
    }
}
