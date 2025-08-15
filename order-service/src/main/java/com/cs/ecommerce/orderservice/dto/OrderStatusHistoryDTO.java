package com.cs.ecommerce.orderservice.dto;

import com.cs.ecommerce.orderservice.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusHistoryDTO {
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private String notes;
    private LocalDateTime createdAt;
}
