package com.ecommerce.payment_service.services;

import com.ecommerce.payment_service.dto.PaymentCreateDTO;
import com.ecommerce.payment_service.dto.PaymentResponseDTO;
import com.ecommerce.payment_service.enums.PaymentMethod;
import com.ecommerce.payment_service.enums.PaymentProvider;
import com.ecommerce.payment_service.enums.PaymentStatus;
import com.ecommerce.payment_service.repositories.PaymentRepo;
import com.ecommerce.payment_service.repositories.PaymentTransactionRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private PaymentTransactionRepo transactionRepo;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentService(paymentRepo, transactionRepo, rabbitTemplate);
    }

    @Test
    void createPaymentStartsPending() {
        PaymentCreateDTO dto = new PaymentCreateDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("99.90"),
                null,
                PaymentMethod.PIX,
                PaymentProvider.INTERNAL
        );
        when(paymentRepo.existsByOrderId(dto.orderId())).thenReturn(false);
        when(paymentRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponseDTO response = paymentService.createPayment(dto);

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.getCurrency()).isEqualTo("BRL");
        assertThat(response.getAmount()).isEqualByComparingTo("99.90");
    }
}
