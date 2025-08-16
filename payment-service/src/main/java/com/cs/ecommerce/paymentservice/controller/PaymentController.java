package com.cs.ecommerce.paymentservice.controller;

import com.cs.ecommerce.paymentservice.dto.*;
import com.cs.ecommerce.paymentservice.service.PaymentService;
import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> processPayment(
            @Valid @RequestBody PaymentRequestDTO paymentRequest,
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Processing payment for order: {}", paymentRequest.getOrderId());
        PaymentResponseDTO response = paymentService.processPayment(paymentRequest, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment processed successfully"));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentDetailsDTO>> getPaymentDetails(
            @PathVariable("paymentId") Long paymentId) {
        log.debug("Fetching payment details for paymentId: {}", paymentId);
        PaymentDetailsDTO paymentDetails = paymentService.getPaymentDetails(paymentId);
        return ResponseEntity.ok(ApiResponse.success(paymentDetails, "Payment details retrieved"));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<PaymentDetailsDTO>>> getPaymentsForOrder(
            @PathVariable("orderId") Long orderId) {
        log.debug("Fetching payments for order: {}", orderId);
        List<PaymentDetailsDTO> payments = paymentService.getPaymentsForOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(payments, "Payments retrieved for order"));
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<ApiResponse<RefundResponseDTO>> processRefund(
            @PathVariable("paymentId") Long paymentId,
            @Valid @RequestBody RefundRequestDTO refundRequest,
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Processing refund for payment: {} by user: {}", paymentId, userId);
        RefundResponseDTO response = paymentService.processRefund(paymentId, refundRequest, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Refund processed successfully"));
    }

}
