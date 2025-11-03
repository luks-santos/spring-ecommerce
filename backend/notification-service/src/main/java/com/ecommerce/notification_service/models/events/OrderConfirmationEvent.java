package com.ecommerce.notification_service.models.events;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderConfirmationEvent extends BaseEvent {
    private Long orderId;
    private Long userId;
    private String userEmail;
    private BigDecimal totalAmount;
    private String orderStatus;
}
