package com.ecommerce.notification_service.services;

import com.ecommerce.notification_service.models.NotificationData;
import com.ecommerce.notification_service.models.NotificationLog;
import com.ecommerce.notification_service.repositories.NotificationLogRepository;
import com.ecommerce.notification_service.templates.EmailNotificationTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailNotificationTemplate emailTemplate;
    private final NotificationLogRepository logRepository;

    @Transactional
    public void sendNotification(NotificationData data) {
        // Enviar notificação usando Template Method
        emailTemplate.send(data);

        // Salvar log
        saveLog(data, "SUCCESS");
    }

    private void saveLog(NotificationData data, String status) {
        NotificationLog log = NotificationLog.builder()
                .recipient(data.getRecipient())
                .subject(data.getSubject())
                .notificationType(data.getNotificationType())
                .templateName(data.getTemplateName())
                .status(status)
                .sentAt(LocalDateTime.now())
                .build();

        logRepository.save(log);
    }
}
