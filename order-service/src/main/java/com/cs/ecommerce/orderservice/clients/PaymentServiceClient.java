package com.cs.ecommerce.orderservice.clients;

import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.sharedmodules.dto.payment.*;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "payment-service", path = "/payments")
public interface PaymentServiceClient {

    @PostMapping("/api/process")
    ApiResponse<PaymentResponseDTO> processPayment(
            @Valid @RequestBody PaymentRequestDTO paymentRequest,
            @RequestHeader("X-User-Id") Long userId);


    @GetMapping("/api/order/{orderId}")
    ApiResponse<List<PaymentDetailsDTO>> getPaymentsForOrder(
            @PathVariable("orderId") Long orderId);

    @PostMapping("/api/{paymentId}/refund")
    ApiResponse<RefundResponseDTO> processRefund(
            @PathVariable("paymentId") Long paymentId,
            @Valid @RequestBody RefundRequestDTO refundRequest,
            @RequestHeader("X-User-Id") Long userId);
}
