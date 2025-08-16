package com.cs.ecommerce.orderservice.clients;

import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "payment-service", path = "/payments")
public interface PaymentServiceClient {

    @PostMapping("/api/process")
    ApiResponse<Map<String, Object>> processPayment(
            @RequestBody Map<String, Object> paymentRequest,
            @RequestHeader("X-User-Id") Long userId);

    @GetMapping("/api/order/{orderId}")
    ApiResponse<List<Map<String, Object>>> getPaymentsForOrder(
            @PathVariable("orderId") Long orderId);

    @PostMapping("/api/{paymentId}/refund")
    ApiResponse<Map<String, Object>> processRefund(
            @PathVariable("paymentId") Long paymentId,
            @RequestBody Map<String, Object> refundRequest,
            @RequestHeader("X-User-Id") Long userId);
}
