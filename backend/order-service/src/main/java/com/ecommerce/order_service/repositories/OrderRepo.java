package com.ecommerce.order_service.repositories;

import com.ecommerce.order_service.entities.Order;
import com.ecommerce.order_service.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepo extends JpaRepository<Order, UUID> {

    List<Order> findByUserId(UUID userId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status")
    List<Order> findByUserIdAndStatus(UUID userId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByPaymentId(UUID paymentId);
}
