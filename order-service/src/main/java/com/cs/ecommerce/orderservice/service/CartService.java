package com.cs.ecommerce.orderservice.service;

import com.cs.ecommerce.orderservice.dto.AddCartItemDTO;
import com.cs.ecommerce.orderservice.dto.CartDTO;
import com.cs.ecommerce.orderservice.dto.UpdateCartItemDTO;

public interface CartService {

    CartDTO getCart(Long userId);

    CartDTO addItemToCart(Long userId, AddCartItemDTO request);

    CartDTO updateCartItem(Long userId, Long itemId, UpdateCartItemDTO request);

    CartDTO removeItemFromCart(Long userId, Long itemId);

    void clearCart(Long userId);
}
