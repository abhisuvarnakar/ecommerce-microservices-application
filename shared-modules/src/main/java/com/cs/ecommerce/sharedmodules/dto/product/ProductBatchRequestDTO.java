package com.cs.ecommerce.sharedmodules.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductBatchRequestDTO {
    private List<Long> productIds;
}
