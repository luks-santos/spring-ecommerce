# Validacao Swagger/OpenAPI

Este documento registra a validacao da configuracao Swagger/OpenAPI dos services HTTP atualmente implementados.

## Services Validados

| Service | Status | Swagger UI | OpenAPI JSON |
| --- | --- | --- | --- |
| `user-service` | Configurado | `http://localhost:8081/swagger-ui.html` | `http://localhost:8081/v3/api-docs` |
| `product-catalog-service` | Configurado | `http://localhost:8082/swagger-ui.html` | `http://localhost:8082/v3/api-docs` |

## Exposicao Pelo Gateway

O Gateway atual possui rotas com `StripPrefix=2`.

OpenAPI JSON pelo Gateway:

| Service | URL |
| --- | --- |
| `user-service` | `http://localhost:8080/api/user/v3/api-docs` |
| `product-catalog-service` | `http://localhost:8080/api/product-catalog/v3/api-docs` |

Para a interface visual do Swagger UI, prefira acessar diretamente a porta do service. A UI do Springdoc pode tentar resolver assets e `v3/api-docs` a partir da raiz do host, o que pode exigir configuracao adicional se for servida pelo Gateway.

## Correcoes Aplicadas

### User Service

Arquivo:

```text
backend/user-service/src/main/java/com/ecommerce/user_service/config/SecurityConfig.java
```

Correcao:

- A liberacao das rotas Swagger/OpenAPI foi ajustada para usar `OrRequestMatcher`.
- Antes havia chamadas encadeadas de `securityMatcher(...)` no mesmo filter chain, o que podia deixar apenas um matcher efetivo.

Rotas liberadas:

```text
/swagger-ui/**
/swagger-ui.html
/v3/api-docs/**
```

### Product Catalog Service

Arquivo:

```text
backend/product-catalog-service/src/main/java/com/ecommerce/product_catalog_service/config/SwaggerConfig.java
```

Correcao:

- Adicionada configuracao OpenAPI explicita com titulo, versao e descricao da API.

## Testes Adicionados

Foram adicionados testes unitarios para validar os metadados OpenAPI:

```text
backend/user-service/src/test/java/com/ecommerce/user_service/config/SwaggerConfigTest.java
backend/product-catalog-service/src/test/java/com/ecommerce/product_catalog_service/config/SwaggerConfigTest.java
```

Validacoes:

- `user-service` expoe titulo `User Service API`.
- `user-service` expoe versao `1.0.0`.
- `user-service` registra security schemes `bearer-key` e `basic-auth`.
- `product-catalog-service` expoe titulo `Product Catalog Service API`.
- `product-catalog-service` expoe versao `1.0.0`.
- `product-catalog-service` expoe descricao da API.

## Documentacao Atualizada

Arquivos atualizados:

```text
backend/user-service/README.md
backend/product-catalog-service/README.md
docs/12-rotas-services-implementados.md
```

Ajustes:

- Rotas antigas foram removidas dos READMEs.
- Rotas reais dos controllers foram documentadas.
- URLs de Swagger UI e OpenAPI JSON foram explicitadas.
- Referencias a endpoints nao implementados, como busca de produtos e health check Actuator, foram removidas das secoes corrigidas.

## Resultado Dos Testes

Comandos executados:

```powershell
cd backend/user-service
.\mvnw.cmd test
```

```powershell
cd backend/product-catalog-service
.\mvnw.cmd test
```

Resultado:

- Os testes nao chegaram a compilar por incompatibilidade de JDK local.
- O ambiente atual usa Java 21:

```text
java 21.0.11
javac 21.0.11
JAVA_HOME=C:\Users\Lucas\.jdks\azul-21.0.11
```

- Antes da migracao, os services principais estavam configurados com `java.version` igual a `23`.
- Erro observado:

```text
release version 23 not supported
```

Depois da padronizacao, valide com JDK 25 ou rode os services via Docker.

## Checklist Manual

Com a stack local em execucao:

```powershell
cd backend
docker compose up --build
```

Validar:

1. Abrir `http://localhost:8081/swagger-ui.html`.
2. Abrir `http://localhost:8081/v3/api-docs`.
3. Abrir `http://localhost:8082/swagger-ui.html`.
4. Abrir `http://localhost:8082/v3/api-docs`.
5. Abrir `http://localhost:8080/api/user/v3/api-docs`.
6. Abrir `http://localhost:8080/api/product-catalog/v3/api-docs`.

Status esperado:

- Swagger UI dos services diretos deve abrir.
- OpenAPI JSON dos services diretos deve retornar JSON.
- OpenAPI JSON pelo Gateway deve retornar JSON quando Eureka, Gateway e os services estiverem registrados corretamente.
