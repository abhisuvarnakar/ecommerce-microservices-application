package com.cs.ecommerce.orderservice.service;

import com.cs.ecommerce.orderservice.dto.*;
import com.cs.ecommerce.orderservice.enums.OrderStatus;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO request, Long userId);

    PaginatedOrderResponse getUserOrders(Long userId, int page, int size, OrderStatus status);

    OrderDTO getOrderDetails(Long orderId, Long userId);

    OrderStatusDTO getOrderStatus(Long orderId, Long userId);

    String cancelOrder(Long userId, Long orderId);

    String updateOrderStatus(Long orderId, OrderStatus status, String trackingNumber);


}
