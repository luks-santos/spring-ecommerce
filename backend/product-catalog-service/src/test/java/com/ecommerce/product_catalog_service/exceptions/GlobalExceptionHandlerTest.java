package com.ecommerce.product_catalog_service.exceptions;

import com.ecommerce.product_catalog_service.dto.ApiErrorDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
    }

    @Test
    void shouldHandleApiException() {
        // Given
        ApiException exception = new ApiException(HttpStatus.BAD_REQUEST, "Erro de teste");

        // When
        ResponseEntity<ApiErrorDTO> response = exceptionHandler.handleApiException(exception, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiErrorDTO errorDTO = response.getBody();
        assertNotNull(errorDTO);
        assertEquals("Erro de teste", errorDTO.getMessage());
        assertEquals("/api/test", errorDTO.getPath());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorDTO.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorDTO.getError());
        assertNotNull(errorDTO.getTimestamp());
    }

    @Test
    void shouldHandleResourceNotFoundException() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Recurso não encontrado");

        // When
        ResponseEntity<ApiErrorDTO> response = exceptionHandler.handleApiException(exception, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiErrorDTO errorDTO = response.getBody();
        assertNotNull(errorDTO);
        assertEquals("Recurso não encontrado", errorDTO.getMessage());
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("product", "name", "O nome é obrigatório");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.Collections.singletonList(fieldError));

        // When
        ResponseEntity<Object> response = exceptionHandler.handleValidationExceptions(exception, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) responseBody.get("errors");
        assertEquals("O nome é obrigatório", errors.get("name"));
    }

    @Test
    void shouldHandleGenericException() {
        // Given
        Exception exception = new RuntimeException("Erro inesperado");

        // When
        ResponseEntity<ApiErrorDTO> response = exceptionHandler.handleGenericException(exception, request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiErrorDTO errorDTO = response.getBody();
        assertNotNull(errorDTO);
        assertEquals("Ocorreu um erro interno no servidor: Erro inesperado", errorDTO.getMessage());
    }
}
