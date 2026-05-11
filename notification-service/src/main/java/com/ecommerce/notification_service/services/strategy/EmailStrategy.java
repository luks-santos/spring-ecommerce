package com.ecommerce.notification_service.services.strategy;

public interface EmailStrategy {
    void sendEmail(String to, String subject, String body);
    String getProviderName();
}
