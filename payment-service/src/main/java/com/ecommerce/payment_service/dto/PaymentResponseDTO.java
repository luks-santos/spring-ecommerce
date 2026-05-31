package com.ecommerce.payment_service.dto;

import com.ecommerce.payment_service.enums.PaymentMethod;
import com.ecommerce.payment_service.enums.PaymentProvider;
import com.ecommerce.payment_service.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private UUID id;
    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentProvider provider;
    private String providerTransactionId;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
