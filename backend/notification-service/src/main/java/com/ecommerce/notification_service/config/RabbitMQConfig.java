package com.ecommerce.notification_service.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EXCHANGE = "user.exchange";
    public static final String ORDER_EXCHANGE = "order.exchange";

    public static final String USER_REGISTRATION_QUEUE = "user.registration.queue";
    public static final String ORDER_CONFIRMATION_QUEUE = "order.confirmation.queue";

    public static final String USER_REGISTRATION_KEY = "user.registration";
    public static final String ORDER_CONFIRMATION_KEY = "order.confirmation";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue userRegistrationQueue() {
        return new Queue(USER_REGISTRATION_QUEUE, true);
    }

    @Bean
    public Binding userRegistrationBinding() {
        return BindingBuilder
                .bind(userRegistrationQueue())
                .to(userExchange())
                .with(USER_REGISTRATION_KEY);
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderConfirmationQueue() {
        return new Queue(ORDER_CONFIRMATION_QUEUE, true);
    }

    @Bean
    public Binding orderConfirmationBinding() {
        return BindingBuilder
                .bind(orderConfirmationQueue())
                .to(orderExchange())
                .with(ORDER_CONFIRMATION_KEY);
    }
}
