package com.ecommerce.product_catalog_service.exceptions;

import org.springframework.http.HttpStatus;

public class InsufficientInventoryException extends ApiException {
    public InsufficientInventoryException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public InsufficientInventoryException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}