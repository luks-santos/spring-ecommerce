package com.ecommerce.payment_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an authenticated user tries to act on a resource they do not own.
 * Handled by {@link GlobalExceptionHandler} as a {@code 403 Forbidden}.
 */
public class ForbiddenException extends ApiException {

    public ForbiddenException(String reason) {
        super(HttpStatus.FORBIDDEN, reason);
    }
}
