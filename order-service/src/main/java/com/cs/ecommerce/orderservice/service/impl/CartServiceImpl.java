package com.cs.ecommerce.orderservice.service.impl;

import com.cs.ecommerce.orderservice.clients.InventoryServiceClient;
import com.cs.ecommerce.orderservice.clients.ProductServiceClient;
import com.cs.ecommerce.orderservice.dto.AddCartItemDTO;
import com.cs.ecommerce.orderservice.dto.CartDTO;
import com.cs.ecommerce.orderservice.dto.CartItemDTO;
import com.cs.ecommerce.orderservice.dto.UpdateCartItemDTO;
import com.cs.ecommerce.orderservice.entities.Cart;
import com.cs.ecommerce.orderservice.entities.CartItem;
import com.cs.ecommerce.orderservice.exceptions.CartItemNotFoundException;
import com.cs.ecommerce.orderservice.exceptions.CartNotFoundException;
import com.cs.ecommerce.orderservice.repository.CartItemRepository;
import com.cs.ecommerce.orderservice.repository.CartRepository;
import com.cs.ecommerce.orderservice.service.CartService;
import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.sharedmodules.dto.inventory.InventoryResponseDTO;
import com.cs.ecommerce.sharedmodules.dto.product.ProductDTO;
import com.cs.ecommerce.sharedmodules.exceptions.InsufficientStockException;
import com.cs.ecommerce.sharedmodules.exceptions.InventoryNotFoundException;
import com.cs.ecommerce.sharedmodules.exceptions.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final ModelMapper modelMapper;

    @Override
    public CartDTO getCart(Long userId) {
        log.info("Fetching cart for user: {}", userId);
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        return mapToCartDTO(cart);
    }

    @Override
    public void addItemToCart(Long userId, AddCartItemDTO request) {
        log.info("Adding item to cart for user: {}, product: {}, quantity: {}",
                userId, request.getProductId(), request.getQuantity());

        ProductDTO product = validateProduct(request.getProductId());
        validateStock(request.getProductId(), request.getQuantity());

        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> createNewCart(userId));

        Optional<CartItem> existingItem = cartItemRepository.findByCartUserIdAndProductId(userId,
                request.getProductId());

        if (existingItem.isPresent()) {
            updateExistingCartItem(existingItem.get(), request.getQuantity());
        } else {
            createNewCartItem(cart, request, product);
        }
    }

    @Override
    public CartDTO updateCartItem(Long userId, Long itemId, UpdateCartItemDTO request) {
        log.info("Updating cart item: {} for user: {} with quantity: {}",
                itemId, userId, request.getQuantity());
        CartItem item = cartItemRepository.findByIdAndCartUserId(itemId, userId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId));
        validateStock(item.getProductId(), request.getQuantity());
        item.setQuantity(request.getQuantity());

        return getCart(userId);
    }

    @Override
    public CartDTO removeItemFromCart(Long userId, Long itemId) {
        log.info("Removing item: {} from cart for user: {}", itemId, userId);
        if (!cartItemRepository.existsByIdAndCartUserId(itemId, userId)) {
            throw new CartItemNotFoundException(itemId);
        }
        cartItemRepository.deleteById(itemId);
        if (cartItemRepository.countItemsByUserId(userId) == 0) {
            cartRepository.deleteByUserId(userId);
        }

        return getCart(userId);
    }

    @Override
    public void clearCart(Long userId) {
        log.info("Clearing cart for user: {}", userId);
        if (!cartRepository.existsByUserId(userId)) {
            throw new CartNotFoundException(userId);
        }
        cartItemRepository.deleteAllByUserId(userId);
        cartRepository.deleteByUserId(userId);
    }

    private Cart createNewCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    private void updateExistingCartItem(CartItem item, Integer additionalQuantity) {
        item.setQuantity(item.getQuantity() + additionalQuantity);
        cartItemRepository.save(item);
    }

    private void createNewCartItem(Cart cart, AddCartItemDTO request, ProductDTO product) {
        CartItem newItem = CartItem.builder()
                .cart(cart)
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .unitPrice(product.getPrice())
                .build();
        cartItemRepository.save(newItem);
    }

    private ProductDTO validateProduct(Long productId) {
        ResponseEntity<ApiResponse<ProductDTO>> response =
                productServiceClient.getProductById(productId);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new ProductNotFoundException(productId);
        }

        return response.getBody().getData();
    }

    private void validateStock(Long productId, Integer quantity) {
        ResponseEntity<ApiResponse<InventoryResponseDTO>> response =
                inventoryServiceClient.getInventory(productId);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new InventoryNotFoundException("Failed to check inventory for product: " + productId);
        }

        InventoryResponseDTO inventory = response.getBody().getData();
        if (inventory.getAvailableStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }
    }

    private CartDTO mapToCartDTO(Cart cart) {
        List<CartItem> items = cart.getItems();
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<CartItemDTO> itemDTOs = items.stream()
                .map(this::mapToCartItemDTO)
                .toList();
        return new CartDTO(cart.getUserId(), itemDTOs, totalAmount, items.size());
    }

    private CartItemDTO mapToCartItemDTO(CartItem item) {
        ProductDTO product = fetchProductDetails(item.getProductId());
        CartItemDTO cartItemDTO = modelMapper.map(item, CartItemDTO.class);
        cartItemDTO.setProductName(product.getName());
        return cartItemDTO;
    }

    private ProductDTO fetchProductDetails(Long productId) {
        try {
            return productServiceClient.getProductById(productId)
                    .getBody()
                    .getData();
        } catch (Exception e) {
            log.error("Failed to fetch product details for ID: {}", productId, e);
            return new ProductDTO();
        }
    }
}
