package com.cs.ecommerce.productservice.service;

import com.cs.ecommerce.productservice.dto.ProductRequestDTO;
import com.cs.ecommerce.productservice.dto.SearchResponseDTO;
import com.cs.ecommerce.sharedmodules.dto.PaginatedResponse;
import com.cs.ecommerce.sharedmodules.dto.product.ProductDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    PaginatedResponse<ProductDTO> getProducts(int page, int size, Long category,
                                              BigDecimal minPrice, BigDecimal maxPrice,
                                              String sortBy, String keyword);

    ProductDTO getProductById(Long productId);

    List<ProductDTO> getProductsByIds(List<Long> productIds);

    ProductDTO createProduct(ProductRequestDTO request);

    void updateProduct(Long productId, ProductRequestDTO request);

    void deleteProduct(Long productId);

    PaginatedResponse<ProductDTO> getProductsByCategory(Long categoryId, int page,
                                                        int size, String sortBy);

    SearchResponseDTO searchProducts(String keyword, Long category,
                                     BigDecimal minPrice, BigDecimal maxPrice,
                                     int page, int size);

    List<String> getProductSuggestions(String keyword);

}
