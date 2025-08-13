package com.cs.ecommerce.orderservice.dto;

import com.cs.ecommerce.orderservice.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusResponseDTO {
    private OrderStatus currentStatus;
    private List<OrderStatusHistoryResponseDTO> statusHistory;
    private LocalDate estimatedDelivery;
    private String trackingNumber;
}
