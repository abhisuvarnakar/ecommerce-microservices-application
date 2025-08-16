package com.cs.ecommerce.paymentservice.service.impl;

import com.cs.ecommerce.paymentservice.dto.PaymentRequestDTO;
import com.cs.ecommerce.paymentservice.dto.RefundRequestDTO;
import com.cs.ecommerce.paymentservice.entities.Payment;
import com.cs.ecommerce.paymentservice.exceptions.PaymentProcessingException;
import com.cs.ecommerce.paymentservice.service.PaymentGatewayService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    private final ObjectMapper objectMapper;
    private static final Random random = new Random();

    @Override
    public PaymentGatewayResponse processPayment(PaymentRequestDTO paymentRequest) {
        log.info("Processing payment for order: {}", paymentRequest.getOrderId());
        boolean isSuccess = random.nextDouble() < 0.9;

        try {
            if (isSuccess) {
                String transactionId = "TXN_" + System.currentTimeMillis();
                String gatewayResponse = objectMapper.writeValueAsString(
                        Map.of(
                                "orderId", paymentRequest.getOrderId(),
                                "amount", paymentRequest.getAmount(),
                                "status", "SUCCESS",
                                "transactionId", transactionId,
                                "gateway", "DummyPaymentGateway",
                                "timestamp", Instant.now().toString()
                        )
                );

                log.info("Payment processed successfully for order: {}, transactionId: {}",
                        paymentRequest.getOrderId(), transactionId);

                return new PaymentGatewayResponse(
                        transactionId,
                        "SUCCESS",
                        "DummyPaymentGateway",
                        gatewayResponse
                );
            } else {
                String gatewayResponse = objectMapper.writeValueAsString(
                        Map.of(
                                "orderId", paymentRequest.getOrderId(),
                                "amount", paymentRequest.getAmount(),
                                "status", "FAILED",
                                "reason", "Insufficient funds",
                                "gateway", "DummyPaymentGateway",
                                "timestamp", Instant.now().toString()
                        )
                );

                log.warn("Payment failed for order: {}", paymentRequest.getOrderId());

                return new PaymentGatewayResponse(
                        null,
                        "FAILED",
                        "DummyPaymentGateway",
                        gatewayResponse
                );
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing payment response for order: {}", paymentRequest.getOrderId(), e);
            throw new PaymentProcessingException("Error processing payment response", e);
        }
    }

    @Override
    public PaymentGatewayResponse processRefund(Payment payment, RefundRequestDTO refundRequest) {
        log.info("Processing refund for payment: {}", payment.getId());
        boolean isSuccess = random.nextDouble() < 0.95;

        try {
            if (isSuccess) {
                String refundId = "RFND_" + System.currentTimeMillis();
                String gatewayResponse = objectMapper.writeValueAsString(
                        Map.of(
                                "paymentId", payment.getId(),
                                "originalTransactionId", payment.getGatewayTransactionId(),
                                "refundAmount", refundRequest.getAmount(),
                                "status", "SUCCESS",
                                "refundId", refundId,
                                "gateway", "DummyPaymentGateway",
                                "timestamp", Instant.now().toString()
                        )
                );

                log.info("Refund processed successfully for payment: {}, refundId: {}",
                        payment.getId(), refundId);

                return new PaymentGatewayResponse(
                        refundId,
                        "SUCCESS",
                        "DummyPaymentGateway",
                        gatewayResponse
                );
            } else {
                String gatewayResponse = objectMapper.writeValueAsString(
                        Map.of(
                                "paymentId", payment.getId(),
                                "originalTransactionId", payment.getGatewayTransactionId(),
                                "refundAmount", refundRequest.getAmount(),
                                "status", "FAILED",
                                "reason", "Transaction not found",
                                "gateway", "DummyPaymentGateway",
                                "timestamp", Instant.now().toString()
                        )
                );

                log.warn("Refund failed for payment: {}", payment.getId());

                return new PaymentGatewayResponse(
                        null,
                        "FAILED",
                        "DummyPaymentGateway",
                        gatewayResponse
                );
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing refund response for payment: {}", payment.getId(), e);
            throw new PaymentProcessingException("Error processing refund response", e);
        }
    }
}
