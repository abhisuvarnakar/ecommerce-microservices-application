package com.cs.ecommerce.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderResponseDTO {
    private Long orderId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private LocalDate estimatedDelivery;
}
