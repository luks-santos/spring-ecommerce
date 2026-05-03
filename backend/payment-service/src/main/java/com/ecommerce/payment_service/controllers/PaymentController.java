package com.ecommerce.payment_service.controllers;

import com.ecommerce.payment_service.dto.PaymentCreateDTO;
import com.ecommerce.payment_service.dto.PaymentResponseDTO;
import com.ecommerce.payment_service.entities.PaymentTransaction;
import com.ecommerce.payment_service.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create payment", description = "Creates a new payment for an order")
    public PaymentResponseDTO createPayment(@Valid @RequestBody PaymentCreateDTO dto) {
        return paymentService.createPayment(dto);
    }

    @PostMapping("/{paymentId}/process")
    @Operation(summary = "Process payment", description = "Processes a pending payment")
    public ResponseEntity<PaymentResponseDTO> processPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.processPayment(paymentId));
    }

    @PostMapping("/{paymentId}/confirm")
    @Operation(summary = "Confirm payment", description = "Confirms a payment as successful")
    public ResponseEntity<PaymentResponseDTO> confirmPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.confirmPayment(paymentId));
    }

    @PostMapping("/{paymentId}/fail")
    @Operation(summary = "Fail payment", description = "Marks a payment as failed")
    public ResponseEntity<PaymentResponseDTO> failPayment(
            @PathVariable UUID paymentId,
            @RequestParam(required = false, defaultValue = "Payment processing failed") String reason) {
        return ResponseEntity.ok(paymentService.failPayment(paymentId, reason));
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund payment", description = "Refunds a successful payment")
    public ResponseEntity<PaymentResponseDTO> refundPayment(
            @PathVariable UUID paymentId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "Refund requested") String reason) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId, amount, reason));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment details by ID")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order", description = "Retrieves payment for a specific order")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get payments by user", description = "Retrieves all payments for a specific user")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }

    @GetMapping("/{paymentId}/transactions")
    @Operation(summary = "Get payment transactions", description = "Retrieves transaction history for a payment")
    public ResponseEntity<List<PaymentTransaction>> getPaymentTransactions(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentTransactions(paymentId));
    }
}
