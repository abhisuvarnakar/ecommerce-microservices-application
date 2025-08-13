package com.cs.ecommerce.inventoryservice.dto;

import com.cs.ecommerce.inventoryservice.enums.StockMovementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockMovementDTO {
    private Long id;
    private Long productId;
    private StockMovementType movementType;
    private Integer quantity;
    private String reason;
    private LocalDateTime createdAt;
}
