package com.cs.ecommerce.productservice.service.impl;

import com.cs.ecommerce.productservice.dto.ProductDTO;
import com.cs.ecommerce.productservice.dto.ProductRequestDTO;
import com.cs.ecommerce.productservice.dto.SearchResponseDTO;
import com.cs.ecommerce.productservice.entities.Category;
import com.cs.ecommerce.productservice.entities.Product;
import com.cs.ecommerce.productservice.repository.CategoryRepository;
import com.cs.ecommerce.productservice.repository.ProductRepository;
import com.cs.ecommerce.productservice.service.ProductService;
import com.cs.ecommerce.sharedmodules.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductSeviceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public PaginatedResponse<ProductDTO> getProducts(int page, int size, Long category,
                                                     BigDecimal minPrice, BigDecimal maxPrice,
                                                     String sortBy, String keyword) {
        Sort sort = createSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepository.findProductsWithFilters(category, minPrice,
                maxPrice, keyword, pageable);
        List<ProductDTO> productDTOList = productPage.getContent().stream()
                .map(this::convertToDTO).toList();

        return PaginatedResponse.of(productDTOList, productPage);
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findByIdAndIsActiveTrue(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        return convertToDTO(product);
    }

    @Override
    public ProductDTO createProduct(ProductRequestDTO request) {
        Category category = categoryRepository.findByNameAndActiveTrue(request.getCategoryName())
                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryName()));

        if (request.getSku() != null && !request.getSku().isBlank()
                && productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("SKU already exists: " + request.getSku());
        }

        Product product = modelMapper.map(request, Product.class);
        product.setCategory(category);
        product.setIsActive(true);

        Product savedProduct = productRepository.save(product);

        // TODO: Publish ProductAdded event

        return convertToDTO(savedProduct);
    }

    @Override
    public void updateProduct(Long productId, ProductRequestDTO request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        if (request.getCategoryName() != null && !request.getCategoryName().isBlank()) {
            Category category = categoryRepository.findByNameAndActiveTrue(request.getCategoryName())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryName()));
            product.setCategory(category);
        }

        if (request.getSku() != null && !request.getSku().trim().isEmpty()
                && productRepository.existsBySkuAndIdNot(request.getSku(), productId)) {
            throw new RuntimeException("SKU already exists: " + request.getSku());
        }

        modelMapper.map(request, product);
        productRepository.save(product);

        // TODO: Publish ProductUpdated event
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // TODO: Check if product is in active orders before deletion

        productRepository.delete(product);

        // TODO: Publish ProductDeleted event
    }

    @Override
    public PaginatedResponse<ProductDTO> getProductsByCategory(Long categoryId, int page,
                                                               int size, String sortBy) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with ID: " + categoryId);
        }

        Sort sort = createSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepository.findByCategoryIdAndIsActiveTrue(
                categoryId, pageable);

        List<ProductDTO> products = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PaginatedResponse.of(products, productPage);
    }

    @Override
    public SearchResponseDTO searchProducts(String keyword, Long category, BigDecimal minPrice,
                                            BigDecimal maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = productRepository.searchProducts(
                keyword, category, minPrice, maxPrice, pageable);

        List<ProductDTO> products = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        List<String> suggestions = productRepository.findProductNameSuggestions(keyword);

        return SearchResponseDTO.builder()
                .products(products)
                .totalResults(productPage.getTotalElements())
                .suggestions(suggestions)
                .build();
    }

    @Override
    public List<String> getProductSuggestions(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new RuntimeException("Keyword cannot be empty");
        }
        return productRepository.findProductNameSuggestions(keyword);
    }

    private Sort createSort(String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return Sort.by("createdAt").descending();
        }

        return switch (sortBy.toLowerCase()) {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "name_asc" -> Sort.by("name").ascending();
            case "name_desc" -> Sort.by("name").descending();
            case "oldest" -> Sort.by("createdAt").ascending();
            default -> Sort.by("createdAt").descending();
        };
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        dto.setProductId(product.getId());
        dto.setCategory(product.getCategory().getName());
        dto.setCategoryId(product.getCategory().getId());
        return dto;
    }
}
