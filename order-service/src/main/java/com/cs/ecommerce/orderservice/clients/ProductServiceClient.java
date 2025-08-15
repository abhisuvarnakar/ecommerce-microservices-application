package com.cs.ecommerce.orderservice.clients;

import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.sharedmodules.dto.product.ProductBatchRequestDTO;
import com.cs.ecommerce.sharedmodules.dto.product.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", path = "/products")
public interface ProductServiceClient {

    @GetMapping("/api/{productId}")
    ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable("productId") Long productId);

    @PostMapping("/api/batchProduct")
    ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByIds(
            @RequestBody ProductBatchRequestDTO productBatchRequestDTO);
}
