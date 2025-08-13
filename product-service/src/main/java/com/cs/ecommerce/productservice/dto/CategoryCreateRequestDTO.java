package com.cs.ecommerce.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryCreateRequestDTO {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    private String parentName;
}
