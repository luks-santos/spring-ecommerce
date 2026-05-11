package com.ecommerce.payment_service.services;

import com.ecommerce.payment_service.dto.PaymentCreateDTO;
import com.ecommerce.payment_service.dto.PaymentResponseDTO;
import com.ecommerce.payment_service.entities.Payment;
import com.ecommerce.payment_service.entities.PaymentTransaction;
import com.ecommerce.payment_service.enums.PaymentStatus;
import com.ecommerce.payment_service.enums.TransactionType;
import com.ecommerce.payment_service.exceptions.BadRequestException;
import com.ecommerce.payment_service.exceptions.ResourceNotFoundException;
import com.ecommerce.payment_service.repositories.PaymentRepo;
import com.ecommerce.payment_service.repositories.PaymentTransactionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final PaymentTransactionRepo transactionRepo;
    private final RabbitTemplate rabbitTemplate;

    private static final String PAYMENT_EXCHANGE = "payment.exchange";
    private static final String PAYMENT_SUCCESS_KEY = "payment.success";
    private static final String PAYMENT_FAILED_KEY = "payment.failed";
    private static final String PAYMENT_REFUNDED_KEY = "payment.refunded";

    /**
     * Create payment
     */
    @Transactional
    public PaymentResponseDTO createPayment(PaymentCreateDTO dto) {
        log.info("Creating payment for order: {}", dto.orderId());

        // Validate if payment already exists for order
        if (paymentRepo.existsByOrderId(dto.orderId())) {
            throw new BadRequestException("Payment already exists for order: " + dto.orderId());
        }

        // Validate amount
        if (dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        // Create payment
        Payment payment = Payment.builder()
                .orderId(dto.orderId())
                .userId(dto.userId())
                .amount(dto.amount())
                .currency(dto.currency() != null ? dto.currency() : "BRL")
                .paymentMethod(dto.paymentMethod())
                .provider(dto.provider())
                .status(PaymentStatus.PENDING)
                .build();

        // Create initial transaction
        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .type(TransactionType.AUTHORIZATION)
                .amount(dto.amount())
                .status(PaymentStatus.PENDING)
                .providerResponse("Payment created")
                .build();

        payment.addTransaction(transaction);
        payment = paymentRepo.save(payment);

        log.info("Payment created successfully: {}", payment.getId());
        return toPaymentResponseDTO(payment);
    }

    /**
     * Process payment (simulated)
     */
    @Transactional
    public PaymentResponseDTO processPayment(UUID paymentId) {
        log.info("Processing payment: {}", paymentId);

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found: " + paymentId));

        // Validate status
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BadRequestException(
                    "Payment cannot be processed. Current status: " + payment.getStatus());
        }

        // Update to processing
        payment.updateStatus(PaymentStatus.PROCESSING);
        paymentRepo.save(payment);

        // Simulate payment processing
        boolean paymentSuccessful = simulatePaymentProcessing(payment);

        if (paymentSuccessful) {
            return confirmPayment(paymentId);
        } else {
            return failPayment(paymentId, "Payment processing failed");
        }
    }

    /**
     * Confirm payment
     */
    @Transactional
    public PaymentResponseDTO confirmPayment(UUID paymentId) {
        log.info("Confirming payment: {}", paymentId);

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found: " + paymentId));

        // Generate provider transaction ID
        payment.setProviderTransactionId("TXN-" + UUID.randomUUID().toString());
        payment.updateStatus(PaymentStatus.SUCCESS);

        // Create success transaction
        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .type(TransactionType.CHARGE)
                .amount(payment.getAmount())
                .status(PaymentStatus.SUCCESS)
                .providerResponse("Payment confirmed successfully")
                .build();

        payment.addTransaction(transaction);
        payment = paymentRepo.save(payment);

        // Publish event
        publishPaymentEvent(PAYMENT_SUCCESS_KEY, payment);

        log.info("Payment confirmed successfully: {}", paymentId);
        return toPaymentResponseDTO(payment);
    }

    /**
     * Fail payment
     */
    @Transactional
    public PaymentResponseDTO failPayment(UUID paymentId, String reason) {
        log.info("Failing payment: {} - Reason: {}", paymentId, reason);

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found: " + paymentId));

        payment.updateStatus(PaymentStatus.FAILED);

        // Create failure transaction
        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .type(TransactionType.CHARGE)
                .amount(payment.getAmount())
                .status(PaymentStatus.FAILED)
                .providerResponse(reason)
                .build();

        payment.addTransaction(transaction);
        payment = paymentRepo.save(payment);

        // Publish event
        publishPaymentEvent(PAYMENT_FAILED_KEY, payment);

        log.info("Payment failed: {}", paymentId);
        return toPaymentResponseDTO(payment);
    }

    /**
     * Refund payment
     */
    @Transactional
    public PaymentResponseDTO refundPayment(UUID paymentId, BigDecimal amount, String reason) {
        log.info("Refunding payment: {} - Amount: {}", paymentId, amount);

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found: " + paymentId));

        // Validate status
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException(
                    "Only successful payments can be refunded. Current status: " + payment.getStatus());
        }

        // Validate amount
        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new BadRequestException(
                    "Refund amount cannot be greater than payment amount");
        }

        // Update status
        if (amount.compareTo(payment.getAmount()) == 0) {
            payment.updateStatus(PaymentStatus.REFUNDED);
        } else {
            payment.updateStatus(PaymentStatus.REFUND_REQUESTED);
        }

        // Create refund transaction
        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .type(TransactionType.REFUND)
                .amount(amount)
                .status(PaymentStatus.REFUNDED)
                .providerResponse(reason)
                .build();

        payment.addTransaction(transaction);
        payment = paymentRepo.save(payment);

        // Publish event
        publishPaymentEvent(PAYMENT_REFUNDED_KEY, payment);

        log.info("Payment refunded successfully: {}", paymentId);
        return toPaymentResponseDTO(payment);
    }

    /**
     * Get payment by ID
     */
    public PaymentResponseDTO getPaymentById(UUID paymentId) {
        log.info("Getting payment: {}", paymentId);

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found: " + paymentId));

        return toPaymentResponseDTO(payment);
    }

    /**
     * Get payment by order ID
     */
    public PaymentResponseDTO getPaymentByOrderId(UUID orderId) {
        log.info("Getting payment for order: {}", orderId);

        Payment payment = paymentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for order: " + orderId));

        return toPaymentResponseDTO(payment);
    }

    /**
     * Get payments by user ID
     */
    public List<PaymentResponseDTO> getPaymentsByUserId(UUID userId) {
        log.info("Getting payments for user: {}", userId);

        List<Payment> payments = paymentRepo.findByUserIdOrderByCreatedAtDesc(userId);

        return payments.stream()
                .map(this::toPaymentResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get payment transactions
     */
    public List<PaymentTransaction> getPaymentTransactions(UUID paymentId) {
        log.info("Getting transactions for payment: {}", paymentId);

        // Verify payment exists
        if (!paymentRepo.existsById(paymentId)) {
            throw new ResourceNotFoundException("Payment not found: " + paymentId);
        }

        return transactionRepo.findByPaymentIdOrderByCreatedAtDesc(paymentId);
    }

    // Private helper methods

    private boolean simulatePaymentProcessing(Payment payment) {
        // Simulate payment processing (90% success rate)
        return Math.random() > 0.1;
    }

    private void publishPaymentEvent(String routingKey, Payment payment) {
        try {
            PaymentResponseDTO dto = toPaymentResponseDTO(payment);
            rabbitTemplate.convertAndSend(PAYMENT_EXCHANGE, routingKey, dto);
            log.info("Published payment event: {} for payment: {}", routingKey, payment.getId());
        } catch (Exception e) {
            log.error("Failed to publish payment event: {}", e.getMessage(), e);
        }
    }

    private PaymentResponseDTO toPaymentResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .provider(payment.getProvider())
                .providerTransactionId(payment.getProviderTransactionId())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
