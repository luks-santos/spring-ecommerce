package com.ecommerce.payment_service.repositories;

import com.ecommerce.payment_service.entities.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction, UUID> {

    List<PaymentTransaction> findByPaymentIdOrderByCreatedAtDesc(UUID paymentId);
}
