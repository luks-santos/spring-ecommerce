package com.ecommerce.notification_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationData {
    private String recipient;
    private String subject;
    private String templateName;
    private Map<String, Object> variables;
    private String notificationType; // EMAIL, SMS, PUSH
}
