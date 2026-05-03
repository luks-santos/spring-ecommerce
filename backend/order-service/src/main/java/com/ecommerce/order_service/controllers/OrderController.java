package com.ecommerce.order_service.controllers;

import com.ecommerce.order_service.dto.OrderCreateDTO;
import com.ecommerce.order_service.dto.OrderResponseDTO;
import com.ecommerce.order_service.dto.OrderUpdateStatusDTO;
import com.ecommerce.order_service.entities.OrderStatusHistory;
import com.ecommerce.order_service.enums.OrderStatus;
import com.ecommerce.order_service.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create order", description = "Creates a new order from cart items")
    public OrderResponseDTO createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        return orderService.createOrder(dto);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieves order details by ID")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user", description = "Retrieves all orders for a specific user")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieves all orders with a specific status")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an order")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderUpdateStatusDTO dto) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, dto));
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels an order")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam(required = false, defaultValue = "Cancelled by user") String reason) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, reason));
    }

    @GetMapping("/{orderId}/history")
    @Operation(summary = "Get order status history", description = "Retrieves the status change history for an order")
    public ResponseEntity<List<OrderStatusHistory>> getOrderStatusHistory(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderStatusHistory(orderId));
    }

    @PatchMapping("/{orderId}/payment/{paymentId}")
    @Operation(summary = "Update payment ID", description = "Updates the payment ID for an order")
    public ResponseEntity<OrderResponseDTO> updatePaymentId(
            @PathVariable UUID orderId,
            @PathVariable UUID paymentId) {
        return ResponseEntity.ok(orderService.updatePaymentId(orderId, paymentId));
    }
}
