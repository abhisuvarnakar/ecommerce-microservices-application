package com.cs.ecommerce.orderservice.controller;

import com.cs.ecommerce.orderservice.dto.AddCartItemDTO;
import com.cs.ecommerce.orderservice.dto.CartDTO;
import com.cs.ecommerce.orderservice.dto.UpdateCartItemDTO;
import com.cs.ecommerce.orderservice.service.CartService;
import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart/api")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@RequestHeader("X-User-Id") Long userId) {
        log.debug("Received request to get cart for user: {}", userId);
        CartDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(ApiResponse.success(cart, "Cart retrieved successfully"));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDTO>> addItemToCart(@RequestHeader("X-User-Id") Long userId,
                                                              @Valid @RequestBody AddCartItemDTO request) {
        log.debug("Received request to add item to cart for user: {}", userId);
        CartDTO cart = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(ApiResponse.success(cart, "Item added to cart successfully"));
    }

    @PutMapping("/{itemId}/update")
    public ResponseEntity<ApiResponse<CartDTO>> updateCartItem(
            @RequestHeader("X-User-Id") Long userId, @PathVariable("itemId") Long itemId,
            @Valid @RequestBody UpdateCartItemDTO request) {
        log.debug("Received request to update cart item: {} for user: {}", itemId, userId);
        CartDTO cart = cartService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(ApiResponse.success(cart, "Cart item updated successfully"));
    }

    @DeleteMapping("/{itemId}/delete")
    public ResponseEntity<ApiResponse<CartDTO>> removeItemFromCart(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("itemId") Long itemId) {
        log.debug("Received request to remove item: {} from cart for user: {}", itemId, userId);
        CartDTO cart = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success(cart, "Item removed from cart successfully"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCart(@RequestHeader("X-User-Id") Long userId) {
        log.debug("Received request to clear cart for user: {}", userId);
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully"));
    }
}
