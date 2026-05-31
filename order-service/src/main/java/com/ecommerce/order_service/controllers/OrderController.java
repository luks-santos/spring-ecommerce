package com.ecommerce.order_service.controllers;

import com.ecommerce.order_service.dto.OrderCreateDTO;
import com.ecommerce.order_service.dto.OrderFromCartDTO;
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
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;

    private UUID extractUserId(JwtAuthenticationToken token) {
        String userId = token.getToken().getClaimAsString("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is missing the userId claim");
        }
        return UUID.fromString(userId);
    }

    private String extractUserEmail(JwtAuthenticationToken token) {
        return token.getToken().getClaimAsString("email");
    }

    private boolean isAdmin(JwtAuthenticationToken token) {
        return token.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isUserAuthorizedForOrder(JwtAuthenticationToken token, OrderResponseDTO order) {
        return isAdmin(token) || order.getUserId().equals(extractUserId(token));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create order", description = "Creates a new order from cart items")
    public OrderResponseDTO createOrder(
            JwtAuthenticationToken token,
            @Valid @RequestBody OrderCreateDTO dto) {
        UUID userId = extractUserId(token);
        String userEmail = extractUserEmail(token);
        OrderCreateDTO secureDto = new OrderCreateDTO(userId, userEmail, dto.shippingAddress(), dto.items());
        return orderService.createOrder(secureDto);
    }

    @PostMapping("/from-cart")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create order from cart", description = "Creates an order from the current user cart and decrements inventory")
    public OrderResponseDTO createOrderFromCart(
            JwtAuthenticationToken token,
            @Valid @RequestBody OrderFromCartDTO dto) {
        UUID userId = extractUserId(token);
        String userEmail = extractUserEmail(token);
        OrderFromCartDTO secureRecord = new OrderFromCartDTO(userId, userEmail, dto.shippingAddress());
        return orderService.createOrderFromCart(secureRecord);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieves order details by ID")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            JwtAuthenticationToken token,
            @PathVariable UUID orderId) {
        OrderResponseDTO order = orderService.getOrderById(orderId);
        if (!isUserAuthorizedForOrder(token, order)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get orders by user", description = "Retrieves all orders for the authenticated user")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(JwtAuthenticationToken token) {
        UUID userId = extractUserId(token);
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status (Admin only)", description = "Retrieves all orders with a specific status")
    public ResponseEntity<?> getOrdersByStatus(
            JwtAuthenticationToken token,
            @PathVariable OrderStatus status) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Admins only");
        }
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status (Admin only)", description = "Updates the status of an order")
    public ResponseEntity<?> updateOrderStatus(
            JwtAuthenticationToken token,
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderUpdateStatusDTO dto) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Admins only");
        }
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, dto));
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels an order")
    public ResponseEntity<?> cancelOrder(
            JwtAuthenticationToken token,
            @PathVariable UUID orderId,
            @RequestParam(required = false, defaultValue = "Cancelled by user") String reason) {
        OrderResponseDTO order = orderService.getOrderById(orderId);
        if (!isUserAuthorizedForOrder(token, order)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(orderService.cancelOrder(orderId, reason));
    }

    @GetMapping("/{orderId}/history")
    @Operation(summary = "Get order status history", description = "Retrieves the status change history for an order")
    public ResponseEntity<?> getOrderStatusHistory(
            JwtAuthenticationToken token,
            @PathVariable UUID orderId) {
        OrderResponseDTO order = orderService.getOrderById(orderId);
        if (!isUserAuthorizedForOrder(token, order)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(orderService.getOrderStatusHistory(orderId));
    }

    @PatchMapping("/{orderId}/payment/{paymentId}")
    @Operation(summary = "Update payment ID (Admin/System only)", description = "Updates the payment ID for an order")
    public ResponseEntity<?> updatePaymentId(
            JwtAuthenticationToken token,
            @PathVariable UUID orderId,
            @PathVariable UUID paymentId) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Admins only");
        }
        return ResponseEntity.ok(orderService.updatePaymentId(orderId, paymentId));
    }
}
