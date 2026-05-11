package com.ecommerce.order_service.enums;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAYMENT_CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    COMPLETED,
    PAYMENT_FAILED,
    CANCELLED,
    REFUNDED
}
