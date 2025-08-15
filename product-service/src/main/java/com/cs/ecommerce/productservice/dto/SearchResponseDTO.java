package com.cs.ecommerce.productservice.dto;

import com.cs.ecommerce.sharedmodules.dto.product.ProductDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchResponseDTO {

    private List<ProductDTO> products;
    private long totalResults;
    private List<String> suggestions;
}
