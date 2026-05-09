# Authentication technical debt

## Current state

- `user-service` issues JWT tokens signed with RSA keys.
- The client obtains a token via `/sign-up` or `/sign-in`.
- `gateway-service` routes requests but does not validate JWT on private routes.
- `product-catalog-service`, `shopping-cart-service`, `order-service`, and `payment-service` are not configured as OAuth2 resource servers.
- Cart, order, and payment endpoints accept `userId` or `userEmail` from the request body or path, not from the authenticated token.
- Internal REST calls between services do not carry authenticated identity.
- RabbitMQ events carry business data but have no `eventId`, `correlationId`, or idempotency policy.

## Accepted risks in the MVP

- A client could operate resources of another user by sending a different `userId`.
- Services called directly bypass gateway-level rules.
- No role or scope-based authorization is enforced in downstream services.
- Duplicate events may be processed more than once.

## Target model

1. `user-service` remains the token issuer.
2. `gateway-service` validates JWT on all private routes.
3. Business services also validate JWT when they can be reached directly.
4. Endpoints stop accepting `userId` as a trusted parameter and derive the user from the token claims (`sub`, `userId`, `email`, roles).
5. JWT claims must include at minimum: `sub`, `userId`, `email`, roles or scopes, and a short expiration.
6. Authorization rules per route:
   - Regular users access only their own resources.
   - Admins manage the product catalog.
   - Internal operations use service-level credentials.
7. Service-to-service communication options:
   - OAuth2 client credentials for internal calls.
   - mTLS between services.
   - Closed internal network combined with gateway validation (simplest for a study environment).
8. RabbitMQ events must include `eventId`, `correlationId`, producer identity, creation timestamp, and consumers must implement idempotency checks.

## Criteria for closing this debt

- Gateway rejects requests without a valid JWT on private routes.
- Cart, order, and payment derive the user from the token, not from an arbitrary client parameter.
- A user cannot read or modify another user's resources.
- Services exposed directly validate JWT or are unreachable from outside the internal network.
- Admin routes require an admin role or scope.
- Critical events have a unique identifier and consumers are idempotent.
- Tests cover missing token, invalid token, cross-user access, and admin-only routes.

## Suggested implementation order

1. Define expected JWT claims and authorization rules per route.
2. Configure `gateway-service` as an OAuth2 resource server.
3. Protect private routes in the gateway.
4. Update cart, order, and payment to derive user identity from the token.
5. Add security tests per service and in the gateway.
6. Review internal REST communication and RabbitMQ events.
