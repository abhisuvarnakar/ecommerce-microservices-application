package com.cs.ecommerce.orderservice.dto;

import com.cs.ecommerce.orderservice.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsResponseDTO {
    private Long orderId;
    private String orderNumber;
    private OrderStatus status;
    private List<OrderItemResponseDTO> items;
    private BigDecimal totalAmount;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private BigDecimal discountAmount;
    private String trackingNumber;
    private LocalDate estimatedDelivery;
    private LocalDateTime createdAt;
    private List<OrderStatusHistoryResponseDTO> timeline;
}
