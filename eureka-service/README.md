# Eureka Service

Service registry and discovery server. Every other service registers here on startup, and the gateway resolves targets by name (`lb://order-service`) instead of hardcoded hosts.

> Part of the [Scalable E-Commerce Platform](../README.md) study project.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Cloud Netflix Eureka Server

## Port

`8761` — no database. Dashboard at http://localhost:8761.

## What to study here

This is the smallest service in the system, and that is the point: it shows **why service
discovery exists**. In a microservices setup, instances come and go and their addresses
are not known up front. Instead of wiring fixed hosts/ports everywhere, each service
registers itself here under a logical name, and callers ask Eureka "where is
`order-service`?" at runtime. That is what makes the gateway route to `lb://order-service`
and what makes horizontal scaling possible without config changes.

A couple of deliberate choices worth noticing in the config: this node is a **server**, so
it does not register with itself (`register-with-eureka: false`, `fetch-registry: false`),
and **self-preservation is disabled**. Self-preservation is a production safety net that
keeps "missing" instances in the registry during network blips; turning it off in this
small study cluster means dead instances disappear quickly, which is friendlier for local
experimentation but would be risky in production.

## Build, run & test

See the [root README](../README.md#running-with-docker) — `docker compose up eureka-service` from the repo root.
