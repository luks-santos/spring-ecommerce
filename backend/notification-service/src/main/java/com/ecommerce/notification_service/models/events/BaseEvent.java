package com.ecommerce.notification_service.models.events;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseEvent implements Serializable {
    private String eventId;
    private LocalDateTime timestamp;

    public BaseEvent() {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }
}
