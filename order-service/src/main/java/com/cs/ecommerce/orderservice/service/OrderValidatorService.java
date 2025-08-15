package com.cs.ecommerce.orderservice.service;

import com.cs.ecommerce.orderservice.dto.CartItemDTO;
import com.cs.ecommerce.orderservice.dto.OrderRequestDTO;
import com.cs.ecommerce.sharedmodules.dto.product.ProductDTO;

import java.util.List;
import java.util.Map;

public interface OrderValidatorService {

    void validateOrderRequest(OrderRequestDTO request);

    Map<Long, ProductDTO> validateProducts(List<CartItemDTO> cartItems);

    void validateInventory(Map<Long, ProductDTO> products, List<CartItemDTO> cartItems);

}
