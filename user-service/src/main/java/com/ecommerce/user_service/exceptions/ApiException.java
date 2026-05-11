package com.ecommerce.user_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


public class ApiException extends ResponseStatusException {

    public ApiException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

    public ApiException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public ApiException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }
}
