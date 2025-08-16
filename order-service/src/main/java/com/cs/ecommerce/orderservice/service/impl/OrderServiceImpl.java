package com.cs.ecommerce.orderservice.service.impl;

import com.cs.ecommerce.orderservice.clients.InventoryServiceClient;
import com.cs.ecommerce.orderservice.clients.PaymentServiceClient;
import com.cs.ecommerce.orderservice.clients.ProductServiceClient;
import com.cs.ecommerce.orderservice.dto.*;
import com.cs.ecommerce.orderservice.entities.Order;
import com.cs.ecommerce.orderservice.entities.OrderItem;
import com.cs.ecommerce.orderservice.entities.OrderStatusHistory;
import com.cs.ecommerce.orderservice.enums.OrderStatus;
import com.cs.ecommerce.orderservice.exceptions.OrderCancellationException;
import com.cs.ecommerce.orderservice.exceptions.OrderNotFoundException;
import com.cs.ecommerce.orderservice.repository.OrderItemRepository;
import com.cs.ecommerce.orderservice.repository.OrderRepository;
import com.cs.ecommerce.orderservice.repository.OrderStatusHistoryRepository;
import com.cs.ecommerce.orderservice.service.CartService;
import com.cs.ecommerce.orderservice.service.OrderService;
import com.cs.ecommerce.orderservice.util.OrderCalculator;
import com.cs.ecommerce.orderservice.util.OrderNumberGenerator;
import com.cs.ecommerce.orderservice.validator.OrderValidator;
import com.cs.ecommerce.sharedmodules.dto.ApiResponse;
import com.cs.ecommerce.sharedmodules.dto.Pagination;
import com.cs.ecommerce.sharedmodules.dto.inventory.ReserveStockRequestDTO;
import com.cs.ecommerce.sharedmodules.dto.inventory.ReserveStockResponseDTO;
import com.cs.ecommerce.sharedmodules.dto.product.ProductDTO;
import com.cs.ecommerce.sharedmodules.enums.ApiStatus;
import com.cs.ecommerce.sharedmodules.exceptions.BusinessException;
import com.cs.ecommerce.sharedmodules.exceptions.ResourceNotFoundException;
import com.cs.ecommerce.sharedmodules.exceptions.ServiceException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final CartService cartService;
    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final OrderValidator orderValidator;
    private final ModelMapper modelMapper;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request, Long userId) {
        Order order = createAndSaveorder(request, userId);
        processPayment(order.getId(), order.getTotalAmount(), request.getPaymentMethod(), userId);

        // TODO: Publish OrderCreated event to Kafka

        return mapToOrderResponseDTO(order);
    }

    @Transactional
    private Order createAndSaveorder(OrderRequestDTO request, Long userId) {
        log.info("Creating order for user: {}", userId);
        orderValidator.validateOrderRequest(request);
        Map<Long, ProductDTO> products = orderValidator.validateProducts(request.getCartItems());
        orderValidator.validateInventory(products, request.getCartItems());

        OrderCalculator.OrderTotals totals =
                OrderCalculator.calculateTotals(request.getCartItems(), products);

        Order order = Order.builder()
                .userId(userId)
                .addressId(request.getAddressId())
                .orderNumber("")
                .status(OrderStatus.PENDING)
                .subtotal(totals.getSubtotal())
                .taxAmount(totals.getTax())
                .shippingAmount(totals.getShipping())
                .discountAmount(totals.getDiscount())
                .totalAmount(totals.getTotal())
                .estimatedDelivery(LocalDate.now().plusDays(3)) // TODO: Make configurable
                .build();
        Order savedOrder = orderRepository.save(order);
        savedOrder.setOrderNumber(OrderNumberGenerator.generateOrderNumber(savedOrder.getId()));
        savedOrder = orderRepository.save(savedOrder);

        List<OrderItem> orderItems = createOrderItems(savedOrder, request.getCartItems(), products);
        orderItemRepository.saveAll(orderItems);

        OrderStatusHistory statusHistory = createStatusHistory(
                savedOrder, null, OrderStatus.PENDING, "Order created");
        orderStatusHistoryRepository.save(statusHistory);

        reserveInventory(userId, savedOrder.getId(), request.getCartItems());

        return savedOrder;
    }

    @Override
    public PaginatedOrderResponse getUserOrders(Long userId, int page, int size,
                                                OrderStatus status) {
        log.info("Fetching orders for user: {}, page: {}, size: {}, status: {}",
                userId, page, size, status);
        validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = status == null ? orderRepository.findByUserId(userId, pageable) :
                orderRepository.findByUserIdAndStatus(userId, status, pageable);

        List<Long> orderIds = orders.getContent().stream().map(Order::getId).toList();
        Map<Long, List<OrderItem>> orderItemsMap = orderItemRepository.findByOrderIds(orderIds)
                .stream()
                .collect(Collectors.groupingBy(oi -> oi.getOrder().getId()));
        Map<Long, List<OrderStatusHistory>> statusHistoryMap =
                orderStatusHistoryRepository.findByOrderIds(orderIds).stream()
                        .collect(Collectors.groupingBy(osh -> osh.getOrder().getId()));

        List<OrderDTO> orderDTOList = orders.getContent().stream()
                .map(order -> mapToOrderDTO(order,
                        orderItemsMap.getOrDefault(order.getId(), Collections.emptyList()),
                        statusHistoryMap.getOrDefault(order.getId(), Collections.emptyList())))
                .toList();

        Pagination pagination = new Pagination(orders.getNumber(), orders.getSize(),
                orders.getTotalElements(), orders.getTotalPages());

        return new PaginatedOrderResponse(orderDTOList, pagination);
    }

    @Override
    public OrderDTO getOrderDetails(Long orderId, Long userId) {
        log.info("Fetching order details for order: {}, user: {}", orderId, userId);
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        List<OrderStatusHistory> history =
                orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtDesc(orderId);

        return mapToOrderDTO(order, items, history);
    }

    @Override
    public OrderStatusDTO getOrderStatus(Long orderId, Long userId) {
        log.info("Fetching order status for order: {}, user: {}", orderId, userId);
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        List<OrderStatusHistory> history =
                orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
        List<OrderStatusHistoryDTO> historyDTOList = history.stream()
                .map(this::mapToStatusHistoryDTO)
                .toList();

        return new OrderStatusDTO(order.getStatus(), historyDTOList, order.getEstimatedDelivery());
    }

    @Override
    @Transactional
    public String cancelOrder(Long userId, Long orderId) {
        log.info("Cancelling order: {} for user: {}", orderId, userId);
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new OrderCancellationException(orderId, order.getStatus());
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        OrderStatusHistory statusHistory = createStatusHistory(order, OrderStatus.PENDING,
                OrderStatus.CANCELLED, "Order cancelled by user");
        orderStatusHistoryRepository.save(statusHistory);

        releaseInventory(userId, orderId);

        // TODO: Publish OrderCancelled event to Kafka

        processRefund(orderId, order.getTotalAmount(), "Dummy reason for cancelling order", userId);

        return "Order cancelled successfully";
    }

    @Override
    @Transactional
    public String updateOrderStatus(Long orderId, OrderStatus status, String trackingNumber) {
        log.info("Updating order: {} to status: {}", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (order.getStatus().equals(status)) {
            throw new BusinessException("Order is already in status: " + status);
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);

        if (status == OrderStatus.SHIPPED && trackingNumber != null) {
            order.setTrackingNumber(trackingNumber);
        }
        orderRepository.save(order);

        OrderStatusHistory statusHistory = createStatusHistory(order, oldStatus, status, "Status " +
                "updated by admin");
        orderStatusHistoryRepository.save(statusHistory);

        // TODO: Publish OrderStatusChanged event to Kafka
        // TODO: Notify user

        return "Order status updated successfully";
    }

    private List<OrderItem> createOrderItems(Order order, List<CartItemDTO> cartItems,
                                             Map<Long, ProductDTO> products) {
        return cartItems.stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(item.getProductId());
                    orderItem.setQuantity(item.getQuantity());

                    ProductDTO product = products.get(item.getProductId());
                    orderItem.setUnitPrice(product.getPrice());
                    orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    return orderItem;
                })
                .toList();
    }

    private OrderStatusHistory createStatusHistory(Order order, OrderStatus oldStatus,
                                                   OrderStatus newStatus, String notes) {
        return OrderStatusHistory.builder()
                .order(order)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .notes(notes)
                .build();
    }

    private void reserveInventory(Long userId, Long orderId, List<CartItemDTO> cartItems) {
        List<ReserveStockRequestDTO> requests = cartItems.stream()
                .map(item ->
                        new ReserveStockRequestDTO(item.getProductId(), item.getQuantity(), orderId)
                ).toList();
        ResponseEntity<ApiResponse<List<ReserveStockResponseDTO>>> response =
                inventoryServiceClient.bulkReserveStock(userId, requests);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new ServiceException("Failed to reserve inventory for order: " + orderId);
        }
    }

    private void releaseInventory(Long userId, Long orderId) {
        ResponseEntity<ApiResponse<String>> response =
                inventoryServiceClient.releaseStockFromOrder(userId
                        , orderId);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Failed to release inventory for order: {}", orderId);
        }
    }

    private void processPayment(Long orderId, BigDecimal totalAmount, String paymentMethod,
                                Long userId) {
        Map<String, Object> request = Map.of("orderId", orderId,
                "amount", totalAmount,
                "paymentMethod", paymentMethod);
        ApiResponse<Map<String, Object>> response = paymentServiceClient.processPayment(request,
                userId);

        if (!ApiStatus.SUCCESS.equals(response.getStatus())) {
            throw new ServiceException("Processing of payment got failed");
        }
    }

    private void processRefund(Long orderId, BigDecimal totalAmount, String reason, Long userId) {
        ApiResponse<List<Map<String, Object>>> paymentResponse =
                paymentServiceClient.getPaymentsForOrder(orderId);
        if (!ApiStatus.SUCCESS.equals(paymentResponse.getStatus())) {
            throw new ResourceNotFoundException("Payment details not found for orderId: " + orderId);
        }

        List<Map<String, Object>> paymentData = paymentResponse.getData();
        for (Map<String, Object> payment : paymentData) {
            Long paymentId = ((Number) payment.get("paymentId")).longValue();
            BigDecimal amount = new BigDecimal(payment.get("amount").toString());
            Map<String, Object> request = Map.of("amount", amount,
                    "reason", reason);
            ApiResponse<Map<String, Object>> response =
                    paymentServiceClient.processRefund(paymentId, request, userId);
            if (!ApiStatus.SUCCESS.equals(response.getStatus())) {
                throw new ServiceException("Refund of payment got failed");
            }
        }
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new BusinessException("Page number must be greater than or equal to 0");
        }
        if (size <= 0 || size > 100) {
            throw new BusinessException("Page size must be between 1 and 100");
        }
    }

    private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        OrderResponseDTO orderResponseDTO = modelMapper.map(order, OrderResponseDTO.class);
        orderResponseDTO.setOrderId(order.getId());
        return orderResponseDTO;
    }

    private OrderDTO mapToOrderDTO(Order order, List<OrderItem> items,
                                   List<OrderStatusHistory> history) {
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        orderDTO.setItems(items.stream()
                .map(item -> {
                    OrderItemDTO itemDTO = modelMapper.map(item, OrderItemDTO.class);
                    try {
                        ProductDTO product = productServiceClient.getProductById(item.getProduct())
                                .getBody().getData();
                        itemDTO.setProductName(product.getName());
                        itemDTO.setProductId(product.getProductId());
                    } catch (Exception e) {
                        log.warn("Failed to fetch product name for product ID: {}",
                                item.getProduct(), e);
                        itemDTO.setProductName("Unknown Product");
                    }
                    return itemDTO;
                }).toList());

        orderDTO.setStatusHistory(history.stream()
                .map(this::mapToStatusHistoryDTO)
                .collect(Collectors.toList()));

        return orderDTO;
    }

    private OrderStatusHistoryDTO mapToStatusHistoryDTO(OrderStatusHistory history) {
        return modelMapper.map(history, OrderStatusHistoryDTO.class);
    }
}
