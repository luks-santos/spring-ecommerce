# Como rodar local

## Pre-requisitos

- Docker e Docker Compose.
- Java 23 para os servicos atuais principais.
- Java 25 se for rodar `notification-service` fora do Docker.
- Maven ou os wrappers `mvnw`/`mvnw.cmd` de cada servico.

## Subir a stack

Na raiz do projeto:

```powershell
cd backend
docker compose up --build
```

Servicos esperados:

- PostgreSQL: `localhost:5433`
- Eureka: `http://localhost:8761`
- Gateway: `http://localhost:8080`
- User Service: `http://localhost:8081`
- Product Catalog Service: `http://localhost:8082`
- RabbitMQ Management: `http://localhost:15672`
- Notification Service: `http://localhost:8084`

## Variaveis de ambiente

O `docker-compose.yml` aceita estas variaveis, com defaults para desenvolvimento:

```text
POSTGRES_USER=ecommerce
POSTGRES_PASSWORD=ecommerce
POSTGRES_DB=ecommerce
POSTGRES_DATABASE_USER=user_db
POSTGRES_DATABASE_PRODUCT=product_db
```

Os servicos recebem:

```text
DATABASE_URL=jdbc:postgresql://postgres-ecommerce:5432/<database>
DB_DRIVER=org.postgresql.Driver
DB_USER=<usuario>
DB_PASSWORD=<senha>
```

## Rodar testes

Os testes atuais usam H2 em memoria e nao exigem PostgreSQL.

```powershell
cd backend/user-service
.\mvnw.cmd test
```

```powershell
cd backend/product-catalog-service
.\mvnw.cmd test
```

```powershell
cd backend/gateway-service
.\mvnw.cmd test
```

```powershell
cd backend/eureka-service
.\mvnw.cmd test
```

```powershell
cd backend/notification-service
.\mvnw.cmd test
```

