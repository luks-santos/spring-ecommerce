package com.ecommerce.payment_service.clients;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * Talks to order-service to confirm that the authenticated user is allowed to
 * act on a given order before a payment is created for it.
 *
 * <p>The user's bearer token is propagated, so order-service applies its own
 * ownership rule: it returns {@code 2xx} only when the caller owns the order (or
 * is an admin) and {@code 403/404} otherwise.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderClient {

    private final RestTemplate restTemplate;

    @Value("${services.order.url:http://localhost:8085}")
    private String orderServiceUrl;

    public boolean currentUserCanAccessOrder(UUID orderId, String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    orderServiceUrl + "/api/orders/{orderId}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Void.class,
                    orderId);
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientResponseException e) {
            // 403 (not owner) or 404 (unknown order) -> not authorized
            log.info("Ownership check for order {} returned {}", orderId, e.getStatusCode());
            return false;
        }
    }
}
