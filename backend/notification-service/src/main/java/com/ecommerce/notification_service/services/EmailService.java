package com.ecommerce.notification_service.services;

import com.ecommerce.notification_service.services.strategy.EmailStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailService {

    private final Map<String, EmailStrategy> strategies;
    private final String selectedProvider;

    public EmailService(
            List<EmailStrategy> strategyList,
            @Value("${notification.email.provider}") String provider
    ) {
        // Converte lista de strategies em um Map (provider -> strategy)
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        EmailStrategy::getProviderName,
                        Function.identity()
                ));

        this.selectedProvider = provider;
        log.info("Email provider configured: {}", provider);
    }

    /**
     * Envia email HTML usando o provider configurado
     */
    public void sendHtmlEmail(String to, String subject, String body) {
        EmailStrategy strategy = strategies.get(selectedProvider);

        if (strategy == null) {
            throw new IllegalStateException(
                    "Email provider not found: " + selectedProvider
            );
        }

        strategy.sendEmail(to, subject, body);
    }

    /**
     * Permite enviar com um provider específico
     */
    public void sendHtmlEmail(String to, String subject, String body, String provider) {
        EmailStrategy strategy = strategies.get(provider);

        if (strategy == null) {
            throw new IllegalArgumentException("Email provider not found: " + provider);
        }

        strategy.sendEmail(to, subject, body);
    }
}