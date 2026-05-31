package com.ecommerce.order_service.repositories;

import com.ecommerce.order_service.entities.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepo extends JpaRepository<ProcessedEvent, String> {
    boolean existsByEventId(String eventId);
}
