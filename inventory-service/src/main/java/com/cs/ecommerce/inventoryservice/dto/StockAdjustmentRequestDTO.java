package com.cs.ecommerce.inventoryservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockAdjustmentRequestDTO {

    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

    private String reason;

    @NotNull
    @Pattern(regexp = "DAMAGE|THEFT|RECOUNT", message = "Type must be DAMAGE, THEFT, or RECOUNT")
    private String type;
}
