package com.ecommerce.notification_service.templates;

import com.ecommerce.notification_service.models.NotificationData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class NotificationTemplate {

    /**
     * Template Method: define o fluxo de envio de notificação
     * Este método NÃO pode ser sobrescrito (final)
     */
    public final void send(NotificationData data) {
        try {
            log.info("Starting notification process for: {}", data.getRecipient());

            // 1. Validar dados
            validateData(data);

            // 2. Construir conteúdo (implementado pelas subclasses)
            String content = buildContent(data);

            // 3. Enviar notificação (implementado pelas subclasses)
            sendNotification(content, data);

            // 4. Registrar log
            logNotification(data);

            log.info("Notification sent successfully to: {}", data.getRecipient());

        } catch (Exception e) {
            log.error("Error sending notification to: {}", data.getRecipient(), e);
            handleError(data, e);
        }
    }

    /**
     * Validações comuns
     */
    protected void validateData(NotificationData data) {
        if (data == null) {
            throw new IllegalArgumentException("Notification data cannot be null");
        }
        if (data.getRecipient() == null || data.getRecipient().isEmpty()) {
            throw new IllegalArgumentException("Recipient cannot be empty");
        }
    }

    /**
     * Métodos abstratos que DEVEM ser implementados pelas subclasses
     */
    protected abstract String buildContent(NotificationData data);
    protected abstract void sendNotification(String content, NotificationData data);

    /**
     * Métodos com implementação padrão (podem ser sobrescritos)
     */
    protected void logNotification(NotificationData data) {
        log.debug("Notification logged: {}", data);
    }

    protected void handleError(NotificationData data, Exception e) {
        log.error("Failed to send notification", e);
        // Aqui você poderia enviar para uma dead letter queue
    }
}
