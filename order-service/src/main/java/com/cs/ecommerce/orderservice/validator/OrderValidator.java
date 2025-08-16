package com.cs.ecommerce.orderservice.validator;

import com.cs.ecommerce.orderservice.clients.InventoryServiceClient;
import com.cs.ecommerce.orderservice.clients.ProductServiceClient;
import com.cs.ecommerce.orderservice.dto.CartItemDTO;
import com.cs.ecommerce.orderservice.dto.OrderRequestDTO;
import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.sharedmodules.dto.inventory.InventoryResponseDTO;
import com.cs.ecommerce.sharedmodules.dto.product.ProductBatchRequestDTO;
import com.cs.ecommerce.sharedmodules.dto.product.ProductDTO;
import com.cs.ecommerce.sharedmodules.exceptions.BusinessException;
import com.cs.ecommerce.sharedmodules.exceptions.InsufficientStockException;
import com.cs.ecommerce.sharedmodules.exceptions.ProductNotFoundException;
import com.cs.ecommerce.sharedmodules.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderValidator {

    private final ProductServiceClient productServiceClient;
    public final InventoryServiceClient inventoryServiceClient;

    public void validateOrderRequest(OrderRequestDTO request) {
        if (request.getAddressId() == null) {
            throw new BusinessException("Shipping address is required");
        }
        if (request.getCartItems() == null || request.getCartItems().isEmpty()) {
            throw new BusinessException("Order must contain at least one item");
        }
    }

    public Map<Long, ProductDTO> validateProducts(List<CartItemDTO> cartItems) {
        List<Long> productIds = cartItems.stream()
                .map(CartItemDTO::getProductId)
                .distinct().toList();

        ResponseEntity<ApiResponse<List<ProductDTO>>> response =
                productServiceClient.getProductsByIds(new ProductBatchRequestDTO(productIds));

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new ServiceException("Failed to validate products");
        }

        List<ProductDTO> products = response.getBody().getData();
        if (products.size() != productIds.size()) {
            List<Long> foundIds = products.stream()
                    .map(ProductDTO::getProductId)
                    .toList();
            List<Long> missingIds = productIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ProductNotFoundException(
                    "Products not found: " + missingIds.stream().map(String::valueOf)
                            .collect(Collectors.joining(",")));
        }

        return products.stream().collect(Collectors.toMap(ProductDTO::getProductId,
                Function.identity()));
    }

    public void validateInventory(Map<Long, ProductDTO> products, List<CartItemDTO> cartItems) {
        Map<Long, Integer> requestedQuantities = new HashMap<>();

        for (CartItemDTO item : cartItems) {
            requestedQuantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            ResponseEntity<ApiResponse<InventoryResponseDTO>> response =
                    inventoryServiceClient.getInventory(productId);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new ServiceException("Failed to check inventory for product: " + productId);
            }

            InventoryResponseDTO inventory = response.getBody().getData();
            if (inventory.getAvailableStock() < quantity) {
                throw new InsufficientStockException(productId, inventory.getAvailableStock(),
                        quantity);
            }
        }
    }
}
