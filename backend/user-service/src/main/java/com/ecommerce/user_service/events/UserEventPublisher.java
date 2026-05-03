package com.ecommerce.user_service.events;

import com.ecommerce.user_service.config.RabbitMQConfig;
import com.ecommerce.user_service.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishRegistration(User user) {
        try {
            UserRegistrationEvent event = UserRegistrationEvent.from(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail()
            );
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.USER_EXCHANGE,
                    RabbitMQConfig.USER_REGISTRATION_KEY,
                    event
            );
            log.info("Published user registration event for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to publish user registration event for user: {}", user.getEmail(), e);
        }
    }
}
