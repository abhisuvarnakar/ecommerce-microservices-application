package com.cs.ecommerce.inventoryservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockUpdateRequestDTO {

    @NotNull
    private Integer quantity;

    @NotNull
    @Pattern(regexp = "ADD|SET", message = "Operation must be ADD or SET")
    private String operation;
}
