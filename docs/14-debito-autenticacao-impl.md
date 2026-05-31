# Authentication Technical Debt — Implementation Guide

## Overview

This guide walks through closing the authentication debt described in `14-debito-autenticacao.md`.
The chosen service-to-service strategy is **closed internal network + gateway validation** (simplest for a study environment).

---

## Step 1 — Enrich JWT claims (`user-service`)

**File**: `user-service/src/main/java/com/ecommerce/user_service/config/jwt/JwtTokenGenerator.java`

Add `userId` and `email` as explicit claims in the access token builder so downstream services can derive the user without trusting request parameters.

```java
// Before (only sub and scope)
JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

// After — add userId and email claims
return encoder.encode(JwtEncoderParameters.from(
    JwsHeader.with(...).build(),
    JwtClaimsSet.builder()
        .issuer("user_service")
        .subject(authentication.getName())          // email
        .claim("userId", user.getId().toString())   // NEW
        .claim("email", user.getEmail())            // NEW
        .claim("scope", scope)
        .issuedAt(now)
        .expiresAt(now.plusSeconds(300))
        .build()
));
```

**Checklist**:
- [X] Inject `UserRepository` or pass the `User` entity into `JwtTokenGenerator`
- [X] Add `userId` claim (UUID as String)
- [X] Add `email` claim
- [X] Verify the token with jwt.io after the change — confirm `userId`, `email`, and `scope` appear

---

## Step 2 — Configure `gateway-service` as OAuth2 Resource Server

### 2.1 Add dependency (`pom.xml`)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

> The gateway uses **Spring WebFlux**, so use `@EnableWebFluxSecurity` — not `@EnableWebSecurity`.

### 2.2 Expose the RSA public key

Copy `publicKey.pem` from `user-service/src/main/resources/certs/` to
`gateway-service/src/main/resources/certs/publicKey.pem`.

### 2.3 Configure JWT in `application-dev.yml`

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          public-key-location: classpath:certs/publicKey.pem
```

### 2.4 Create `SecurityConfig.java`

```java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex
                // Public routes
                .pathMatchers(HttpMethod.POST, "/api/user/sign-in").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/user/sign-up").permitAll()
                .pathMatchers(HttpMethod.GET,  "/api/product-catalog/**").permitAll()
                // Admin-only writes on product catalog
                .pathMatchers(HttpMethod.POST, "/api/product-catalog/**").hasAuthority("SCOPE_WRITE")
                .pathMatchers(HttpMethod.PUT,  "/api/product-catalog/**").hasAuthority("SCOPE_WRITE")
                .pathMatchers(HttpMethod.DELETE, "/api/product-catalog/**").hasAuthority("SCOPE_WRITE")
                // Everything else requires authentication
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
    }
}
```

**Checklist**:
- [ ] Dependency added to `pom.xml`
- [ ] `publicKey.pem` copied to gateway resources
- [ ] `application-dev.yml` (and `application-homolog.yml`) updated with `public-key-location`
- [ ] `SecurityConfig` created with WebFlux annotations
- [ ] Curl test: request without token to `/api/carts/**` returns `401`
- [ ] Curl test: request with valid token reaches the service

---

## Step 3 — Configure downstream services as OAuth2 Resource Servers

Repeat for `shopping-cart-service`, `order-service`, and `payment-service`.

### 3.1 Add dependency to each `pom.xml`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

### 3.2 Copy `publicKey.pem` to each service

```
shopping-cart-service/src/main/resources/certs/publicKey.pem
order-service/src/main/resources/certs/publicKey.pem
payment-service/src/main/resources/certs/publicKey.pem
```

### 3.3 Configure `application.yml` in each service

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          public-key-location: classpath:certs/publicKey.pem
```

### 3.4 Create `SecurityConfig.java` in each service (Servlet, not WebFlux)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
    }
}
```

**Checklist**:
- [ ] `pom.xml` updated in all three services
- [ ] `publicKey.pem` copied to all three services
- [ ] `application.yml` updated in all three services
- [ ] `SecurityConfig` created in all three services

---

## Step 4 — Derive user identity from token in controllers

Remove `userId` from request paths and extract it from the `JwtAuthenticationToken`.

### Helper method (add to each controller or a shared utility)

```java
private UUID extractUserId(JwtAuthenticationToken token) {
    return UUID.fromString(token.getToken().getClaimAsString("userId"));
}
```

### 4.1 `shopping-cart-service` — `CartController.java`

```java
// Before
@PostMapping("/user/{userId}/items")
public ResponseEntity<?> addItem(@PathVariable UUID userId, @RequestBody ...) { ... }

// After
@PostMapping("/items")
public ResponseEntity<?> addItem(JwtAuthenticationToken token, @RequestBody ...) {
    UUID userId = extractUserId(token);
    ...
}
```

Apply the same pattern to: update quantity, remove item, clear cart, delete cart, and get cart.

### 4.2 `order-service` — `OrderController.java`

```java
// Before
@GetMapping("/user/{userId}")
public ResponseEntity<?> getByUser(@PathVariable UUID userId) { ... }

// After
@GetMapping("/my-orders")
public ResponseEntity<?> getMyOrders(JwtAuthenticationToken token) {
    UUID userId = extractUserId(token);
    ...
}
```

### 4.3 `payment-service` — `PaymentController.java`

```java
// Before
@GetMapping("/user/{userId}")
public ResponseEntity<?> getByUser(@PathVariable UUID userId) { ... }

// After
@GetMapping("/my-payments")
public ResponseEntity<?> getMyPayments(JwtAuthenticationToken token) {
    UUID userId = extractUserId(token);
    ...
}
```

**Checklist**:
- [ ] `CartController`: all endpoints use token, `{userId}` removed from paths
- [ ] `OrderController`: get-by-user uses token
- [ ] `PaymentController`: get-by-user uses token
- [ ] Update gateway routing if path prefixes changed
- [ ] Update any frontend/Postman collection to reflect new paths

---

## Step 5 — Authorization rules per route

### 5.1 `product-catalog-service`

Add the dependency and `SecurityConfig` as in Step 3, but with role-based rules:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.GET, "/**").permitAll()
    .requestMatchers(HttpMethod.POST, "/**").hasAuthority("SCOPE_WRITE")
    .requestMatchers(HttpMethod.PUT, "/**").hasAuthority("SCOPE_WRITE")
    .requestMatchers(HttpMethod.DELETE, "/**").hasAuthority("SCOPE_WRITE")
    .anyRequest().authenticated()
)
```

> The `user-service` grants `WRITE` and `DELETE` scopes only to users with the `ADMIN` role.
> Spring Security maps JWT scopes to authorities prefixed with `SCOPE_`.

**Checklist**:
- [ ] `product-catalog-service` configured as resource server
- [ ] GET endpoints remain public
- [ ] POST/PUT/DELETE require `SCOPE_WRITE`

---

## Step 6 — RabbitMQ event structure

### 6.1 Create a base event record

Create this in a shared location or duplicate per service (no shared library needed for a study project):

```java
public record EventMetadata(
    String eventId,        // UUID.randomUUID().toString()
    String correlationId,  // propagated from the originating HTTP request or prior event
    String producer,       // service name, e.g. "order-service"
    Instant occurredAt     // Instant.now()
) {}
```

### 6.2 Update `UserRegistrationEvent`

**File**: `user-service/.../events/UserRegistrationEvent.java`

```java
public record UserRegistrationEvent(
    String eventId,
    String correlationId,   // NEW
    String producer,        // NEW — "user-service"
    Instant occurredAt,     // rename from timestamp
    UUID userId,
    String fullName,
    String username,
    String email
) {}
```

### 6.3 Create typed event classes for `order-service` and `payment-service`

**Order events** (`order-service/.../events/`):

```java
public record OrderCreatedEvent(
    String eventId,
    String correlationId,
    String producer,
    Instant occurredAt,
    UUID orderId,
    UUID userId,
    BigDecimal totalAmount
) {}
```

**Payment events** (`payment-service/.../events/`):

```java
public record PaymentProcessedEvent(
    String eventId,
    String correlationId,
    String producer,
    Instant occurredAt,
    UUID paymentId,
    UUID orderId,
    String status   // SUCCESS | FAILED | REFUNDED
) {}
```

### 6.4 Idempotency in consumers

Each consumer must check whether the `eventId` was already processed:

```java
@RabbitListener(queues = "order.payment.success.queue")
public void onPaymentSuccess(PaymentProcessedEvent event) {
    if (processedEventRepository.existsByEventId(event.eventId())) {
        return; // already handled
    }
    // business logic
    processedEventRepository.save(new ProcessedEvent(event.eventId()));
}
```

**Checklist**:
- [ ] `UserRegistrationEvent` updated with `correlationId` and `producer`
- [ ] `OrderCreatedEvent` (and other order events) created
- [ ] `PaymentProcessedEvent` created
- [ ] Consumers updated to use typed records instead of `Map<String, Object>`
- [ ] Idempotency check added (at minimum for payment-related events)

---

## Step 7 — Tests

### Per service — unit/integration tests to write

| Scenario | Expected result |
|---|---|
| Request with no token to private route | `401 Unauthorized` |
| Request with expired token | `401 Unauthorized` |
| Request with valid token, wrong user's resource | `403 Forbidden` |
| Admin token on product catalog write route | `200 OK` |
| Client token on product catalog write route | `403 Forbidden` |
| Valid token, own resource | `200 OK` |

### Example test skeleton (Spring Boot test)

```java
@SpringBootTest
@AutoConfigureMockMvc
class CartControllerSecurityTest {

    @Autowired MockMvc mockMvc;

    @Test
    void addItem_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/api/carts/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockJwtAuth(claims = @OpenIdClaims(sub = "user-a@test.com"))
    void addItem_withValidToken_returns200() throws Exception {
        // ...
    }
}
```

**Checklist**:
- [ ] Gateway security tests
- [ ] Cart service security tests
- [ ] Order service security tests
- [ ] Payment service security tests
- [ ] Product catalog admin-route tests

---

## Completion criteria (from the debt document)

- [ ] Gateway rejects requests without a valid JWT on private routes
- [ ] Cart, order, and payment derive the user from the token, not from a client parameter
- [ ] A user cannot read or modify another user's resources
- [ ] Admin routes require `SCOPE_WRITE`
- [ ] Critical events have a unique `eventId` and consumers are idempotent
- [ ] Tests cover missing token, invalid token, cross-user access, and admin-only routes
