package com.ecommerce.order_service.services;

import com.ecommerce.order_service.dto.OrderCreateDTO;
import com.ecommerce.order_service.dto.OrderItemDTO;
import com.ecommerce.order_service.dto.OrderResponseDTO;
import com.ecommerce.order_service.enums.OrderStatus;
import com.ecommerce.order_service.repositories.OrderItemRepo;
import com.ecommerce.order_service.repositories.OrderRepo;
import com.ecommerce.order_service.repositories.OrderStatusHistoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private OrderItemRepo orderItemRepo;

    @Mock
    private OrderStatusHistoryRepo statusHistoryRepo;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepo, orderItemRepo, statusHistoryRepo, rabbitTemplate);
    }

    @Test
    void createOrderCalculatesTotalAndStartsCreated() {
        OrderItemDTO item = new OrderItemDTO(UUID.randomUUID(), 2, new BigDecimal("15.50"));
        OrderCreateDTO dto = new OrderCreateDTO(
                UUID.randomUUID(),
                "customer@example.com",
                "Rua Teste, 123",
                List.of(item)
        );
        when(orderRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDTO response = orderService.createOrder(dto);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.getTotalAmount()).isEqualByComparingTo("31.00");
        assertThat(response.getTotalItems()).isEqualTo(2);
        assertThat(response.getUserEmail()).isEqualTo("customer@example.com");
    }

    @Test
    void confirmPaymentUpdatesStatusAndPaymentId() {
        UUID orderId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        com.ecommerce.order_service.entities.Order order = com.ecommerce.order_service.entities.Order.builder()
                .id(orderId)
                .userId(UUID.randomUUID())
                .status(OrderStatus.CREATED)
                .totalAmount(BigDecimal.TEN)
                .shippingAddress("Rua Teste, 123")
                .build();
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDTO response = orderService.confirmPayment(orderId, paymentId);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.PAYMENT_CONFIRMED);
        assertThat(response.getPaymentId()).isEqualTo(paymentId);
    }
}
