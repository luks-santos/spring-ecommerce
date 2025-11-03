package com.ecommerce.notification_service.models.events;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserRegistrationEvent extends BaseEvent {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
}
