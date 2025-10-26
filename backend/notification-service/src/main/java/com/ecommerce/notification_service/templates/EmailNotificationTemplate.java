package com.ecommerce.notification_service.templates;


import com.ecommerce.notification_service.models.NotificationData;
import com.ecommerce.notification_service.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationTemplate extends NotificationTemplate {

    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    @Override
    protected String buildContent(NotificationData data) {
        Context context = new Context();
        context.setVariables(data.getVariables());

        return templateEngine.process(data.getTemplateName(), context);
    }

    @Override
    protected void sendNotification(String content, NotificationData data) {
        emailService.sendHtmlEmail(
                data.getRecipient(),
                data.getSubject(),
                content
        );
    }

    @Override
    protected void validateData(NotificationData data) {
        super.validateData(data);

        // Validação específica de email
        if (!data.getRecipient().contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        if (data.getSubject() == null || data.getSubject().isEmpty()) {
            throw new IllegalArgumentException("Email subject cannot be empty");
        }
    }
}
