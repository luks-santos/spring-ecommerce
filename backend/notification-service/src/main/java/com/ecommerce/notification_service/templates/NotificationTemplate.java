package com.ecommerce.notification_service.templates;

import com.ecommerce.notification_service.models.NotificationData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class NotificationTemplate {

    public final void send(NotificationData data) {
        try {
            log.info("Starting notification process for: {}", data.getRecipient());

            validateData(data);
            String content = buildContent(data);
            sendNotification(content, data);
            logNotification(data);

            log.info("Notification sent successfully to: {}", data.getRecipient());

        } catch (Exception e) {
            log.error("Error sending notification to: {}", data.getRecipient(), e);
            handleError(data, e);
        }
    }

    protected void validateData(NotificationData data) {
        if (data == null) {
            throw new IllegalArgumentException("Notification data cannot be null");
        }
        if (data.getRecipient() == null || data.getRecipient().isEmpty()) {
            throw new IllegalArgumentException("Recipient cannot be empty");
        }
    }

    protected abstract String buildContent(NotificationData data);
    protected abstract void sendNotification(String content, NotificationData data);

    protected void logNotification(NotificationData data) {
        log.debug("Notification logged: {}", data);
    }

    protected void handleError(NotificationData data, Exception e) {
        log.error("Failed to send notification", e);
    }
}
