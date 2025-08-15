package com.cs.ecommerce.orderservice.controller;

import com.cs.ecommerce.orderservice.dto.*;
import com.cs.ecommerce.orderservice.enums.OrderStatus;
import com.cs.ecommerce.orderservice.service.OrderService;
import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody OrderRequestDTO request) {
        log.debug("Received request to create order for user: {}", userId);
        OrderResponseDTO order = orderService.createOrder(request, userId);
        return ResponseEntity.ok(ApiResponse.success(order, "Order created successfully"));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<PaginatedOrderResponse>> getUserOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "status", required = false) OrderStatus status) {
        log.debug("Received request to get orders for user: {}, page: {}, size: {}, status: {}",
                userId, page, size, status);
        PaginatedOrderResponse response = orderService.getUserOrders(userId, page, size, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Orders retrieved successfully"));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderDetails(
            @PathVariable("orderId") Long orderId,
            @RequestHeader("X-User-Id") Long userId) {
        log.debug("Received request to get order details for order: {}, user: {}", orderId, userId);
        OrderDTO order = orderService.getOrderDetails(orderId, userId);
        return ResponseEntity.ok(ApiResponse.success(order, "Order details retrieved successfully"));
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderStatusDTO>> getOrderStatus(
            @PathVariable("orderId") Long orderId,
            @RequestHeader("X-User-Id") Long userId) {
        log.debug("Received request to get order status for order: {}, user: {}", orderId, userId);
        OrderStatusDTO status = orderService.getOrderStatus(orderId, userId);
        return ResponseEntity.ok(ApiResponse.success(status, "Order status retrieved successfully"));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("orderId") Long orderId) {
        log.debug("Received request to cancel order: {} by user: {}", orderId, userId);
        String message = orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PutMapping("/{orderId}/status")
    //@RequireRole(Role.ADMIN)
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(
            @PathVariable("orderId") Long orderId,
            @RequestParam("status") OrderStatus status,
            @RequestParam(name = "trackingNumber", required = false) String trackingNumber) {
        log.debug("Received request to update order: {} to status: {}", orderId, status);
        String message = orderService.updateOrderStatus(orderId, status, trackingNumber);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}

