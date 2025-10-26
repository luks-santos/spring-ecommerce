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

    // Nome das exchanges
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String ORDER_EXCHANGE = "order.exchange";

    // Nome das queues
    public static final String USER_REGISTRATION_QUEUE = "user.registration.queue";
    public static final String ORDER_CONFIRMATION_QUEUE = "order.confirmation.queue";

    // Routing keys
    public static final String USER_REGISTRATION_KEY = "user.registration";
    public static final String ORDER_CONFIRMATION_KEY = "order.confirmation";

    /**
     * Conversor de mensagens para JSON
     * Permite enviar/receber objetos Java como JSON
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Template para enviar mensagens (será usado por outros serviços)
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    // ========== USER REGISTRATION ==========

    /**
     * Exchange para eventos de usuário
     * Topic exchange permite roteamento flexível com routing keys
     */
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    /**
     * Fila para receber eventos de registro de usuário
     */
    @Bean
    public Queue userRegistrationQueue() {
        return new Queue(USER_REGISTRATION_QUEUE, true); // true = durable (persiste após restart)
    }

    /**
     * Binding: conecta a queue com a exchange usando uma routing key
     */
    @Bean
    public Binding userRegistrationBinding() {
        return BindingBuilder
                .bind(userRegistrationQueue())
                .to(userExchange())
                .with(USER_REGISTRATION_KEY);
    }

    // ========== ORDER CONFIRMATION ==========

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
