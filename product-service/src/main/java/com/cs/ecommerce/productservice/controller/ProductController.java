package com.cs.ecommerce.productservice.controller;

import com.cs.ecommerce.productservice.annotation.RequireRole;
import com.cs.ecommerce.productservice.dto.ProductDTO;
import com.cs.ecommerce.productservice.dto.ProductRequestDTO;
import com.cs.ecommerce.productservice.dto.SearchResponseDTO;
import com.cs.ecommerce.productservice.service.ProductService;
import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.sharedmodules.dto.PaginatedResponse;
import com.cs.ecommerce.sharedmodules.enums.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductDTO>>> getProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "category", required = false) Long category,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "sortBy", defaultValue = "newest") String sortBy,
            @RequestParam(name = "keyword", required = false) String keyword) {

        PaginatedResponse<ProductDTO> products = productService.getProducts(
                page, size, category, minPrice, maxPrice, sortBy, keyword);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(
            @PathVariable("productId") Long productId) {
        ProductDTO product = productService.getProductById(productId);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved successfully"));
    }

    @PostMapping
    @RequireRole(Role.ADMIN)
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @Valid @RequestBody ProductRequestDTO request) {
        ProductDTO productDTO = productService.createProduct(request);
        return ResponseEntity.ok(ApiResponse.success(productDTO, "Product created successfully"));
    }

    @PutMapping("/{productId}")
    @RequireRole(Role.ADMIN)
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody ProductRequestDTO request) {
        productService.updateProduct(productId, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully"));
    }

    @DeleteMapping("/{productId}")
    @RequireRole(Role.ADMIN)
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductDTO>>> getProductsByCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "newest") String sortBy) {

        PaginatedResponse<ProductDTO> products = productService.getProductsByCategory(
                categoryId, page, size, sortBy);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<SearchResponseDTO>> searchProducts(
            @RequestParam(name = "keyword") @NotBlank String keyword,
            @RequestParam(name = "category", required = false) Long category,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        SearchResponseDTO searchResult = productService.searchProducts(
                keyword, category, minPrice, maxPrice, page, size);
        return ResponseEntity.ok(ApiResponse.success(searchResult, "Search completed successfully"));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getProductSuggestions(
            @RequestParam(name = "keyword") @NotBlank String keyword) {
        List<String> suggestions = productService.getProductSuggestions(keyword);
        return ResponseEntity.ok(ApiResponse.success(suggestions, "Suggestions retrieved successfully"));
    }

}
