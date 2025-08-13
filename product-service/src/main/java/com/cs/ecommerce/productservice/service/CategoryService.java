package com.cs.ecommerce.productservice.service;

import com.cs.ecommerce.productservice.dto.CategoryCreateRequestDTO;
import com.cs.ecommerce.productservice.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    List<CategoryDTO> getAllCategories();

    CategoryDTO createCategory(CategoryCreateRequestDTO request);
}
