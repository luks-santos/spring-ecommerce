package com.ecommerce.shopping_cart_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange
    public static final String CART_EXCHANGE = "cart.exchange";

    // Queues
    public static final String CART_CHECKOUT_QUEUE = "cart.checkout.queue";
    public static final String CART_ABANDONED_QUEUE = "cart.abandoned.queue";

    // Routing Keys
    public static final String CART_CHECKOUT_KEY = "cart.checkout";
    public static final String CART_ABANDONED_KEY = "cart.abandoned";

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    @Bean
    public TopicExchange cartExchange() {
        return new TopicExchange(CART_EXCHANGE);
    }

    @Bean
    public Queue cartCheckoutQueue() {
        return new Queue(CART_CHECKOUT_QUEUE, true);
    }

    @Bean
    public Queue cartAbandonedQueue() {
        return new Queue(CART_ABANDONED_QUEUE, true);
    }

    @Bean
    public Binding cartCheckoutBinding() {
        return BindingBuilder
                .bind(cartCheckoutQueue())
                .to(cartExchange())
                .with(CART_CHECKOUT_KEY);
    }

    @Bean
    public Binding cartAbandonedBinding() {
        return BindingBuilder
                .bind(cartAbandonedQueue())
                .to(cartExchange())
                .with(CART_ABANDONED_KEY);
    }
}
