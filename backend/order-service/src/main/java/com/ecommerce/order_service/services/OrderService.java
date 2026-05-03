package com.ecommerce.order_service.services;

import com.ecommerce.order_service.clients.CartResponseDTO;
import com.ecommerce.order_service.dto.*;
import com.ecommerce.order_service.entities.Order;
import com.ecommerce.order_service.entities.OrderItem;
import com.ecommerce.order_service.entities.OrderStatusHistory;
import com.ecommerce.order_service.enums.OrderStatus;
import com.ecommerce.order_service.exceptions.BadRequestException;
import com.ecommerce.order_service.exceptions.ResourceNotFoundException;
import com.ecommerce.order_service.repositories.OrderItemRepo;
import com.ecommerce.order_service.repositories.OrderRepo;
import com.ecommerce.order_service.repositories.OrderStatusHistoryRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final OrderStatusHistoryRepo statusHistoryRepo;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;

    @Value("${services.shopping-cart.url:http://localhost:8083}")
    private String shoppingCartServiceUrl;

    @Value("${services.product-catalog.url:http://localhost:8082}")
    private String productCatalogServiceUrl;

    private static final String ORDER_EXCHANGE = "order.exchange";
    private static final String ORDER_CREATED_KEY = "order.created";
    private static final String ORDER_CONFIRMED_KEY = "order.confirmation";
    private static final String ORDER_SHIPPED_KEY = "order.shipped";
    private static final String ORDER_CANCELLED_KEY = "order.cancelled";

    /**
     * Create a new order
     */
    @Transactional
    public OrderResponseDTO createOrder(OrderCreateDTO dto) {
        log.info("Creating order for user: {}", dto.userId());

        // Validate items
        if (dto.items() == null || dto.items().isEmpty()) {
            throw new BadRequestException("Order must have at least one item");
        }

        // Create order
        Order order = Order.builder()
                .userId(dto.userId())
                .userEmail(dto.userEmail())
                .status(OrderStatus.CREATED)
                .shippingAddress(dto.shippingAddress())
                .totalAmount(BigDecimal.ZERO)
                .build();

        // Add items
        for (OrderItemDTO itemDTO : dto.items()) {
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .productId(itemDTO.productId())
                    .quantity(itemDTO.quantity())
                    .unitPrice(itemDTO.unitPrice())
                    .build();
            item.calculateSubtotal();
            order.addItem(item);
        }

        // Calculate total
        order.setTotalAmount(order.calculateTotal());

        // Add initial status history
        order.updateStatus(OrderStatus.CREATED, "Order created");

        // Save order
        order = orderRepo.save(order);

        // Publish event
        publishOrderEvent(ORDER_CREATED_KEY, order);

        log.info("Order created successfully: {}", order.getId());
        return toOrderResponseDTO(order);
    }

    /**
     * Get order by ID
     */
    public OrderResponseDTO getOrderById(UUID orderId) {
        log.info("Getting order: {}", orderId);

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found: " + orderId));

        return toOrderResponseDTO(order);
    }

    /**
     * Get all orders by user ID
     */
    public List<OrderResponseDTO> getOrdersByUserId(UUID userId) {
        log.info("Getting orders for user: {}", userId);

        List<Order> orders = orderRepo.findByUserIdOrderByCreatedAtDesc(userId);

        return orders.stream()
                .map(this::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by status
     */
    public List<OrderResponseDTO> getOrdersByStatus(OrderStatus status) {
        log.info("Getting orders with status: {}", status);

        List<Order> orders = orderRepo.findByStatus(status);

        return orders.stream()
                .map(this::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update order status
     */
    @Transactional
    public OrderResponseDTO updateOrderStatus(UUID orderId, OrderUpdateStatusDTO dto) {
        log.info("Updating order status: {} to {}", orderId, dto.status());

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found: " + orderId));

        // Validate status transition
        validateStatusTransition(order.getStatus(), dto.status());

        // Update status
        order.updateStatus(dto.status(), dto.notes());
        order = orderRepo.save(order);

        // Publish event based on status
        publishStatusChangeEvent(dto.status(), order);

        log.info("Order status updated successfully: {}", orderId);
        return toOrderResponseDTO(order);
    }

    /**
     * Cancel order
     */
    @Transactional
    public OrderResponseDTO cancelOrder(UUID orderId, String reason) {
        log.info("Cancelling order: {}", orderId);

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found: " + orderId));

        // Validate if order can be cancelled
        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.COMPLETED ||
                order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException(
                    "Cannot cancel order in status: " + order.getStatus());
        }

        // Update status
        order.updateStatus(OrderStatus.CANCELLED, reason);
        order = orderRepo.save(order);

        // Publish event
        publishOrderEvent(ORDER_CANCELLED_KEY, order);

        log.info("Order cancelled successfully: {}", orderId);
        return toOrderResponseDTO(order);
    }

    /**
     * Get order status history
     */
    public List<OrderStatusHistory> getOrderStatusHistory(UUID orderId) {
        log.info("Getting status history for order: {}", orderId);

        // Verify order exists
        if (!orderRepo.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found: " + orderId);
        }

        return statusHistoryRepo.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    /**
     * Update payment ID
     */
    @Transactional
    public OrderResponseDTO updatePaymentId(UUID orderId, UUID paymentId) {
        log.info("Updating payment ID for order: {}", orderId);

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found: " + orderId));

        order.setPaymentId(paymentId);
        order = orderRepo.save(order);

        return toOrderResponseDTO(order);
    }

    @Transactional
    public OrderResponseDTO createOrderFromCart(OrderFromCartDTO dto) {
        log.info("Creating order from cart for user: {}", dto.userId());

        CartResponseDTO cart = restTemplate.getForObject(
                shoppingCartServiceUrl + "/api/carts/user/{userId}",
                CartResponseDTO.class,
                dto.userId()
        );

        if (cart == null || cart.items() == null || cart.items().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        List<OrderItemDTO> orderItems = cart.items().stream()
                .map(item -> new OrderItemDTO(item.productId(), item.quantity(), item.price()))
                .toList();

        for (OrderItemDTO item : orderItems) {
            decrementInventory(item);
        }

        OrderResponseDTO order = createOrder(new OrderCreateDTO(
                dto.userId(),
                dto.userEmail(),
                dto.shippingAddress(),
                orderItems
        ));

        clearCart(dto.userId());
        return order;
    }

    @Transactional
    public OrderResponseDTO confirmPayment(UUID orderId, UUID paymentId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        order.setPaymentId(paymentId);
        if (order.getStatus() != OrderStatus.PAYMENT_CONFIRMED) {
            order.updateStatus(OrderStatus.PAYMENT_CONFIRMED, "Payment confirmed");
        }
        order = orderRepo.save(order);
        publishOrderEvent(ORDER_CONFIRMED_KEY, order);
        return toOrderResponseDTO(order);
    }

    @Transactional
    public OrderResponseDTO failPayment(UUID orderId, UUID paymentId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        order.setPaymentId(paymentId);
        if (order.getStatus() != OrderStatus.PAYMENT_FAILED) {
            order.updateStatus(OrderStatus.PAYMENT_FAILED, "Payment failed");
        }
        order = orderRepo.save(order);
        return toOrderResponseDTO(order);
    }

    @Transactional
    public OrderResponseDTO refundPayment(UUID orderId, UUID paymentId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        order.setPaymentId(paymentId);
        if (order.getStatus() != OrderStatus.REFUNDED) {
            order.updateStatus(OrderStatus.REFUNDED, "Payment refunded");
        }
        order = orderRepo.save(order);
        return toOrderResponseDTO(order);
    }

    // Private helper methods

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Basic validation - can be extended with more complex rules
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot change status of cancelled order");
        }

        if (currentStatus == OrderStatus.COMPLETED) {
            throw new BadRequestException("Cannot change status of completed order");
        }

        if (currentStatus == OrderStatus.REFUNDED) {
            throw new BadRequestException("Cannot change status of refunded order");
        }
    }

    private void publishStatusChangeEvent(OrderStatus status, Order order) {
        switch (status) {
            case PAYMENT_CONFIRMED:
                publishOrderEvent(ORDER_CONFIRMED_KEY, order);
                break;
            case SHIPPED:
                publishOrderEvent(ORDER_SHIPPED_KEY, order);
                break;
            case CANCELLED:
                publishOrderEvent(ORDER_CANCELLED_KEY, order);
                break;
            default:
                // No event for other statuses
                break;
        }
    }

    private void publishOrderEvent(String routingKey, Order order) {
        try {
            Object payload = ORDER_CONFIRMED_KEY.equals(routingKey)
                    ? toOrderConfirmationEvent(order)
                    : toOrderResponseDTO(order);
            rabbitTemplate.convertAndSend(ORDER_EXCHANGE, routingKey, payload);
            log.info("Published order event: {} for order: {}", routingKey, order.getId());
        } catch (Exception e) {
            log.error("Failed to publish order event: {}", e.getMessage(), e);
            // Don't throw exception - event publishing failure should not break the operation
        }
    }

    // Mapper methods

    private OrderResponseDTO toOrderResponseDTO(Order order) {
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(this::toOrderItemResponseDTO)
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .userEmail(order.getUserEmail())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .paymentId(order.getPaymentId())
                .items(itemDTOs)
                .totalItems(order.getTotalItems())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private void decrementInventory(OrderItemDTO item) {
        restTemplate.patchForObject(
                productCatalogServiceUrl + "/inventories/product/{productId}/remove?qty={quantity}",
                null,
                Object.class,
                item.productId(),
                item.quantity()
        );
    }

    private void clearCart(UUID userId) {
        try {
            restTemplate.delete(shoppingCartServiceUrl + "/api/carts/user/{userId}/clear", userId);
        } catch (Exception e) {
            log.warn("Order created, but cart cleanup failed for user: {}", userId, e);
        }
    }

    private Map<String, Object> toOrderConfirmationEvent(Order order) {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", order.getId());
        event.put("userId", order.getUserId());
        event.put("userEmail", order.getUserEmail());
        event.put("totalAmount", order.getTotalAmount());
        event.put("orderStatus", order.getStatus().name());
        return event;
    }

    private OrderItemResponseDTO toOrderItemResponseDTO(OrderItem item) {
        return OrderItemResponseDTO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
