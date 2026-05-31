package com.ecommerce.order_service.config;

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
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    // Queues
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_CONFIRMED_QUEUE = "order.confirmed.queue";
    public static final String ORDER_SHIPPED_QUEUE = "order.shipped.queue";
    public static final String ORDER_DELIVERED_QUEUE = "order.delivered.queue";
    public static final String ORDER_CANCELLED_QUEUE = "order.cancelled.queue";
    public static final String ORDER_PAYMENT_SUCCESS_QUEUE = "order.payment.success.queue";
    public static final String ORDER_PAYMENT_FAILED_QUEUE = "order.payment.failed.queue";
    public static final String ORDER_PAYMENT_REFUNDED_QUEUE = "order.payment.refunded.queue";

    // Routing Keys
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String ORDER_CONFIRMED_KEY = "order.confirmed";
    public static final String ORDER_SHIPPED_KEY = "order.shipped";
    public static final String ORDER_DELIVERED_KEY = "order.delivered";
    public static final String ORDER_CANCELLED_KEY = "order.cancelled";
    public static final String PAYMENT_SUCCESS_KEY = "payment.success";
    public static final String PAYMENT_FAILED_KEY = "payment.failed";
    public static final String PAYMENT_REFUNDED_KEY = "payment.refunded";

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
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    // Queues
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE, true);
    }

    @Bean
    public Queue orderConfirmedQueue() {
        return new Queue(ORDER_CONFIRMED_QUEUE, true);
    }

    @Bean
    public Queue orderShippedQueue() {
        return new Queue(ORDER_SHIPPED_QUEUE, true);
    }

    @Bean
    public Queue orderDeliveredQueue() {
        return new Queue(ORDER_DELIVERED_QUEUE, true);
    }

    @Bean
    public Queue orderCancelledQueue() {
        return new Queue(ORDER_CANCELLED_QUEUE, true);
    }

    @Bean
    public Queue orderPaymentSuccessQueue() {
        return new Queue(ORDER_PAYMENT_SUCCESS_QUEUE, true);
    }

    @Bean
    public Queue orderPaymentFailedQueue() {
        return new Queue(ORDER_PAYMENT_FAILED_QUEUE, true);
    }

    @Bean
    public Queue orderPaymentRefundedQueue() {
        return new Queue(ORDER_PAYMENT_REFUNDED_QUEUE, true);
    }

    // Bindings
    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder
                .bind(orderCreatedQueue())
                .to(orderExchange())
                .with(ORDER_CREATED_KEY);
    }

    @Bean
    public Binding orderConfirmedBinding() {
        return BindingBuilder
                .bind(orderConfirmedQueue())
                .to(orderExchange())
                .with(ORDER_CONFIRMED_KEY);
    }

    @Bean
    public Binding orderShippedBinding() {
        return BindingBuilder
                .bind(orderShippedQueue())
                .to(orderExchange())
                .with(ORDER_SHIPPED_KEY);
    }

    @Bean
    public Binding orderDeliveredBinding() {
        return BindingBuilder
                .bind(orderDeliveredQueue())
                .to(orderExchange())
                .with(ORDER_DELIVERED_KEY);
    }

    @Bean
    public Binding orderCancelledBinding() {
        return BindingBuilder
                .bind(orderCancelledQueue())
                .to(orderExchange())
                .with(ORDER_CANCELLED_KEY);
    }

    @Bean
    public Binding orderPaymentSuccessBinding() {
        return BindingBuilder
                .bind(orderPaymentSuccessQueue())
                .to(paymentExchange())
                .with(PAYMENT_SUCCESS_KEY);
    }

    @Bean
    public Binding orderPaymentFailedBinding() {
        return BindingBuilder
                .bind(orderPaymentFailedQueue())
                .to(paymentExchange())
                .with(PAYMENT_FAILED_KEY);
    }

    @Bean
    public Binding orderPaymentRefundedBinding() {
        return BindingBuilder
                .bind(orderPaymentRefundedQueue())
                .to(paymentExchange())
                .with(PAYMENT_REFUNDED_KEY);
    }
}
