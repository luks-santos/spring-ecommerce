# Rotas dos Services Implementados

Este documento lista as rotas HTTP atualmente implementadas no projeto e expostas pelo Gateway quando a stack local esta em execucao.

Tambem foi criada uma colecao Postman importavel em:

```text
docs/postman/ecommerce-services.postman_collection.json
```

## URLs Base

| Componente | URL |
| --- | --- |
| Gateway | `http://localhost:8080` |
| Eureka Dashboard | `http://localhost:8761` |
| User Service direto | `http://localhost:8081` |
| Product Catalog Service direto | `http://localhost:8082` |
| Notification Service direto | `http://localhost:8084` |
| RabbitMQ Management | `http://localhost:15672` |

## Swagger

Os services HTTP atualmente implementados ja possuem Springdoc/OpenAPI configurado:

| Service | Swagger UI | OpenAPI JSON |
| --- | --- | --- |
| User Service | `http://localhost:8081/swagger-ui.html` | `http://localhost:8081/v3/api-docs` |
| Product Catalog Service | `http://localhost:8082/swagger-ui.html` | `http://localhost:8082/v3/api-docs` |

Observacoes:

- Para validar a UI do Swagger, prefira acessar diretamente o service na porta dele.
- O OpenAPI JSON tambem pode ser acessado pelo Gateway:
  - User Service: `http://localhost:8080/api/user/v3/api-docs`
  - Product Catalog Service: `http://localhost:8080/api/product-catalog/v3/api-docs`
- `notification-service` atualmente trabalha por eventos RabbitMQ e nao possui controllers REST implementados.
- `eureka-service` e `gateway-service` sao servicos de infraestrutura e nao possuem API de negocio documentada por Swagger.
- O Gateway atual usa `StripPrefix=2`, entao algumas rotas do `user-service` ficam com `/api` duplicado na URL externa, como `/api/user/api/account/logged-user`.

## User Service

Rotas via Gateway:

| Metodo | Rota | Autenticacao | Descricao |
| --- | --- | --- | --- |
| `POST` | `/api/user/sign-up` | Publica | Cadastra usuario e retorna token JWT |
| `POST` | `/api/user/sign-in` | Basic Auth | Autentica usuario e retorna token JWT |
| `POST` | `/api/user/refresh-token` | Bearer refresh token | Gera novo access token |
| `GET` | `/api/user/api/account/logged-user` | Bearer access token | Retorna usuario autenticado |
| `PUT` | `/api/user/api/account/update_profile` | Bearer access token | Atualiza perfil do usuario autenticado |

Rotas diretas no service:

| Metodo | Rota direta |
| --- | --- |
| `POST` | `http://localhost:8081/sign-up` |
| `POST` | `http://localhost:8081/sign-in` |
| `POST` | `http://localhost:8081/refresh-token` |
| `GET` | `http://localhost:8081/api/account/logged-user` |
| `PUT` | `http://localhost:8081/api/account/update_profile` |

### Exemplo: Sign Up

```http
POST http://localhost:8080/api/user/sign-up
Content-Type: application/json
```

```json
{
  "firstName": "Lucas",
  "lastName": "Silva",
  "email": "lucas@example.com",
  "phone": "11999999999",
  "address": "Rua Exemplo, 123",
  "password": "123456"
}
```

### Exemplo: Update Profile

```http
PUT http://localhost:8080/api/user/api/account/update_profile
Authorization: Bearer <access_token>
Content-Type: application/json
```

```json
{
  "firstName": "Lucas",
  "lastName": "Silva",
  "email": "lucas@example.com",
  "phone": "11888888888",
  "address": "Rua Atualizada, 456"
}
```

## Product Catalog Service

### Categories

Rotas via Gateway:

| Metodo | Rota | Descricao |
| --- | --- | --- |
| `GET` | `/api/product-catalog/categories` | Lista categorias |
| `GET` | `/api/product-catalog/categories/{id}` | Busca categoria por ID |
| `POST` | `/api/product-catalog/categories` | Cria categoria |
| `PUT` | `/api/product-catalog/categories/{id}` | Atualiza categoria |
| `DELETE` | `/api/product-catalog/categories/{id}` | Remove categoria |

Exemplo de body:

```json
{
  "name": "Eletronicos"
}
```

### Products

Rotas via Gateway:

| Metodo | Rota | Descricao |
| --- | --- | --- |
| `GET` | `/api/product-catalog/products` | Lista produtos |
| `GET` | `/api/product-catalog/products/{id}` | Busca produto por ID |
| `GET` | `/api/product-catalog/products/category/{categoryId}` | Lista produtos por categoria |
| `POST` | `/api/product-catalog/products` | Cria produto |
| `PUT` | `/api/product-catalog/products/{id}` | Atualiza produto |
| `DELETE` | `/api/product-catalog/products/{id}` | Remove produto |

Exemplo de body:

```json
{
  "name": "Notebook Gamer",
  "description": "Notebook com placa de video dedicada",
  "price": 5999.90,
  "categoryId": "00000000-0000-0000-0000-000000000000"
}
```

### Inventories

Rotas via Gateway:

| Metodo | Rota | Descricao |
| --- | --- | --- |
| `GET` | `/api/product-catalog/inventories` | Lista inventarios |
| `GET` | `/api/product-catalog/inventories/product/{productId}` | Busca inventario por produto |
| `POST` | `/api/product-catalog/inventories` | Cria inventario |
| `PUT` | `/api/product-catalog/inventories/product/{productId}` | Atualiza inventario por produto |
| `PATCH` | `/api/product-catalog/inventories/product/{productId}/add?qty={qty}` | Adiciona quantidade ao estoque |
| `PATCH` | `/api/product-catalog/inventories/product/{productId}/remove?qty={qty}` | Remove quantidade do estoque |

Exemplo de body:

```json
{
  "productId": "00000000-0000-0000-0000-000000000000",
  "quantity": 10
}
```

## Notification Service

O `notification-service` nao possui rotas REST de negocio no estado atual. Ele consome eventos RabbitMQ:

- evento de cadastro de usuario;
- evento de confirmacao de pedido.

Console RabbitMQ:

```text
http://localhost:15672
```

Credenciais locais no Docker Compose:

```text
usuario: guest
senha: guest
```

## Ordem Recomendada Para Testar no Postman

1. `POST /api/user/sign-up`.
2. Copiar ou deixar a collection salvar o `access_token`.
3. `GET /api/user/api/account/logged-user`.
4. `POST /api/product-catalog/categories`.
5. `POST /api/product-catalog/products`.
6. `POST /api/product-catalog/inventories`.
7. Testar `PATCH add/remove` no inventario.
