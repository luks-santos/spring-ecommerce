package com.ecommerce.notification_service.services.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsoleStrategy implements EmailStrategy {

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("========== EMAIL (CONSOLE) ==========");
        log.info("To: {}", to);
        log.info("Subject: {}", subject);
        log.info("Body:\n{}", body);
        log.info("=====================================");
    }

    @Override
    public String getProviderName() {
        return "console";
    }
}
