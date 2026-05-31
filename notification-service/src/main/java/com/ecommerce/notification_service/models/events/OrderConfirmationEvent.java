package com.ecommerce.notification_service.models.events;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderConfirmationEvent extends BaseEvent {
    private UUID orderId;
    private UUID userId;
    private String userEmail;
    private BigDecimal totalAmount;
    private String orderStatus;
}
