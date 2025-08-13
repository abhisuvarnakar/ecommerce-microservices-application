package com.cs.ecommerce.productservice.controller;

import com.cs.ecommerce.productservice.annotation.RequireRole;
import com.cs.ecommerce.productservice.dto.CategoryCreateRequestDTO;
import com.cs.ecommerce.productservice.dto.CategoryDTO;
import com.cs.ecommerce.productservice.service.CategoryService;
import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.sharedmodules.enums.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved " +
                "successfully"));
    }

    @PostMapping
    @RequireRole(Role.ADMIN)
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(
            @Valid @RequestBody CategoryCreateRequestDTO request) {
        CategoryDTO category = categoryService.createCategory(request);
        return ResponseEntity.ok(ApiResponse.success(category, "Category created " +
                "successfully"));
    }

}
