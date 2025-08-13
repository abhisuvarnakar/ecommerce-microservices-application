package com.cs.ecommerce.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponseDTO {
    private Long productId;
    private Integer availableStock;
    private Integer reservedStock;
    private Integer totalStock;
}
