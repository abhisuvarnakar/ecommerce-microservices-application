package com.cs.ecommerce.orderservice.dto;

import com.cs.ecommerce.orderservice.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long userId;
    private Long addressId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private BigDecimal discountAmount;
    private LocalDate estimatedDelivery;
    private String trackingNumber;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;
    private List<OrderStatusHistoryDTO> statusHistory;
}
