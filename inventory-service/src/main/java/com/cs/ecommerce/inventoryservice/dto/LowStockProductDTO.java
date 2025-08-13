package com.cs.ecommerce.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LowStockProductDTO {
    private Long productId;
    private Integer currentStock;
    private Integer reorderLevel;
}
