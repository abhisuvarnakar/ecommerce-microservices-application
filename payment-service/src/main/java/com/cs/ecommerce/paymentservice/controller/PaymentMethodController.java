package com.cs.ecommerce.paymentservice.controller;

import com.cs.ecommerce.paymentservice.dto.AddPaymentMethodResponseDTO;
import com.cs.ecommerce.paymentservice.dto.PaymentMethodRequestDTO;
import com.cs.ecommerce.paymentservice.dto.PaymentMethodResponseDTO;
import com.cs.ecommerce.paymentservice.service.PaymentMethodService;
import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/methods")
@RequiredArgsConstructor
@Slf4j
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<PaymentMethodResponseDTO>>> getUserPaymentMethods(
            @PathVariable("userId") Long userId) {
        log.debug("Fetching payment methods for user: {}", userId);
        List<PaymentMethodResponseDTO> methods = paymentMethodService.getUserPaymentMethods(userId);
        return ResponseEntity.ok(ApiResponse.success(methods, "Payment methods retrieved"));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<AddPaymentMethodResponseDTO>> addUserPaymentMethod(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody PaymentMethodRequestDTO request) {
        log.info("Adding payment method for user: {}", userId);
        AddPaymentMethodResponseDTO response = paymentMethodService.addUserPaymentMethod(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, response.getMessage()));
    }

    @DeleteMapping("/{methodId}")
    public ResponseEntity<ApiResponse<String>> deleteUserPaymentMethod(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("methodId") Long methodId) {
        log.info("Deleting payment method: {} for user: {}", methodId, userId);
        paymentMethodService.deleteUserPaymentMethod(userId, methodId);
        return ResponseEntity.ok(ApiResponse.success("Payment method deleted successfully"));
    }

    @PutMapping("/{userId}/default/{methodId}")
    public ResponseEntity<ApiResponse<String>> setDefaultPaymentMethod(
            @PathVariable("userId") Long userId,
            @PathVariable("methodId") Long methodId) {
        log.info("Setting payment method: {} as default for user: {}", methodId, userId);
        paymentMethodService.setDefaultPaymentMethod(userId, methodId);
        return ResponseEntity.ok(ApiResponse.success("Default payment method updated successfully"));
    }
}
