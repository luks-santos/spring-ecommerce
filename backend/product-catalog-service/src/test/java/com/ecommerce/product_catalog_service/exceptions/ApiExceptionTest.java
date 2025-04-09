package com.ecommerce.product_catalog_service.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiExceptionTest {

    @Test
    void shouldCreateApiExceptionWithBadRequestStatusByDefault() {
        ApiException exception = new ApiException("Erro de teste");

        assertEquals(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(exception.getStatusCode().value()));
        assertEquals("Erro de teste", exception.getReason());
    }

    @Test
    void shouldCreateApiExceptionWithSpecificStatus() {
        ApiException exception = new ApiException(HttpStatus.NOT_FOUND, "Recurso não encontrado");

        assertEquals(HttpStatus.NOT_FOUND, HttpStatus.valueOf(exception.getStatusCode().value()));
        assertEquals("Recurso não encontrado", exception.getReason());
    }

    @Test
    void shouldCreateResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Produto não encontrado");

        assertEquals(HttpStatus.NOT_FOUND, HttpStatus.valueOf(exception.getStatusCode().value()));
        assertEquals("Produto não encontrado", exception.getReason());
    }

    @Test
    void shouldCreateInsufficientInventoryException() {
        InsufficientInventoryException exception = new InsufficientInventoryException("Estoque insuficiente");

        assertEquals(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(exception.getStatusCode().value()));
        assertEquals("Estoque insuficiente", exception.getReason());
    }
}
