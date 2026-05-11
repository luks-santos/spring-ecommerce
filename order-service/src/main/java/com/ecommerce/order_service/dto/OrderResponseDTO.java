package com.ecommerce.order_service.dto;

import com.ecommerce.order_service.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private UUID id;
    private UUID userId;
    private String userEmail;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private UUID paymentId;
    private List<OrderItemResponseDTO> items;
    private int totalItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
