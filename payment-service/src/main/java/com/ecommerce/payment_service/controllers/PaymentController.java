package com.ecommerce.payment_service.controllers;

import com.ecommerce.payment_service.clients.OrderClient;
import com.ecommerce.payment_service.dto.PaymentCreateDTO;
import com.ecommerce.payment_service.dto.PaymentResponseDTO;
import com.ecommerce.payment_service.entities.PaymentTransaction;
import com.ecommerce.payment_service.exceptions.ForbiddenException;
import com.ecommerce.payment_service.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management endpoints")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderClient orderClient;

    private UUID extractUserId(JwtAuthenticationToken token) {
        String userId = token.getToken().getClaimAsString("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is missing the userId claim");
        }
        return UUID.fromString(userId);
    }

    private boolean isAdmin(JwtAuthenticationToken token) {
        return token.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isUserAuthorizedForPayment(JwtAuthenticationToken token, PaymentResponseDTO payment) {
        return isAdmin(token) || payment.getUserId().equals(extractUserId(token));
    }

    /** Loads a payment and fails with 403 when the caller is not its owner (or an admin). */
    private PaymentResponseDTO requireOwnedPayment(JwtAuthenticationToken token, UUID paymentId) {
        PaymentResponseDTO payment = paymentService.getPaymentById(paymentId);
        if (!isUserAuthorizedForPayment(token, payment)) {
            throw new ForbiddenException("You are not allowed to access this payment");
        }
        return payment;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create payment", description = "Creates a new payment for an order")
    public PaymentResponseDTO createPayment(
            JwtAuthenticationToken token,
            @Valid @RequestBody PaymentCreateDTO dto) {
        UUID userId = extractUserId(token);
        // Business rule: a user may only pay for an order they own. The orderId
        // comes from the request body, so confirm ownership against order-service.
        if (!orderClient.currentUserCanAccessOrder(dto.orderId(), token.getToken().getTokenValue())) {
            throw new ForbiddenException("You cannot create a payment for an order that is not yours");
        }
        PaymentCreateDTO secureDto = new PaymentCreateDTO(dto.orderId(), userId, dto.amount(), dto.currency(), dto.paymentMethod(), dto.provider());
        return paymentService.createPayment(secureDto);
    }

    @PostMapping("/{paymentId}/process")
    @Operation(summary = "Process payment", description = "Processes a pending payment")
    public PaymentResponseDTO processPayment(
            JwtAuthenticationToken token,
            @PathVariable UUID paymentId) {
        requireOwnedPayment(token, paymentId);
        return paymentService.processPayment(paymentId);
    }

    @PostMapping("/{paymentId}/confirm")
    @Operation(summary = "Confirm payment", description = "Confirms a payment as successful")
    public PaymentResponseDTO confirmPayment(
            JwtAuthenticationToken token,
            @PathVariable UUID paymentId) {
        requireOwnedPayment(token, paymentId);
        return paymentService.confirmPayment(paymentId);
    }

    @PostMapping("/{paymentId}/fail")
    @Operation(summary = "Fail payment", description = "Marks a payment as failed")
    public PaymentResponseDTO failPayment(
            JwtAuthenticationToken token,
            @PathVariable UUID paymentId,
            @RequestParam(required = false, defaultValue = "Payment processing failed") String reason) {
        requireOwnedPayment(token, paymentId);
        return paymentService.failPayment(paymentId, reason);
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund payment", description = "Refunds a successful payment")
    public PaymentResponseDTO refundPayment(
            JwtAuthenticationToken token,
            @PathVariable UUID paymentId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "Refund requested") String reason) {
        requireOwnedPayment(token, paymentId);
        return paymentService.refundPayment(paymentId, amount, reason);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment details by ID")
    public PaymentResponseDTO getPaymentById(
            JwtAuthenticationToken token,
            @PathVariable UUID paymentId) {
        return requireOwnedPayment(token, paymentId);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order", description = "Retrieves payment for a specific order")
    public PaymentResponseDTO getPaymentByOrderId(
            JwtAuthenticationToken token,
            @PathVariable UUID orderId) {
        PaymentResponseDTO payment = paymentService.getPaymentByOrderId(orderId);
        if (!isUserAuthorizedForPayment(token, payment)) {
            throw new ForbiddenException("You are not allowed to access this payment");
        }
        return payment;
    }

    @GetMapping("/my-payments")
    @Operation(summary = "Get payments by user", description = "Retrieves all payments for the authenticated user")
    public List<PaymentResponseDTO> getPaymentsByUserId(JwtAuthenticationToken token) {
        return paymentService.getPaymentsByUserId(extractUserId(token));
    }

    @GetMapping("/{paymentId}/transactions")
    @Operation(summary = "Get payment transactions", description = "Retrieves transaction history for a payment")
    public List<PaymentTransaction> getPaymentTransactions(
            JwtAuthenticationToken token,
            @PathVariable UUID paymentId) {
        requireOwnedPayment(token, paymentId);
        return paymentService.getPaymentTransactions(paymentId);
    }
}
