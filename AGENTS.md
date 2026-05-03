# AGENTS.md

Guia operacional para agentes trabalhando neste repositório.

## Contexto do projeto

Este repositório implementa uma plataforma de e-commerce escalável baseada no projeto
`Scalable E-Commerce Platform` do roadmap.sh.

Objetivo arquitetural:
- Backend Java/Spring Boot em microserviços.
- Serviços independentes para usuários, catálogo, carrinho, pedidos, pagamentos e notificações.
- API Gateway para entrada externa.
- Service discovery com Eureka.
- Docker Compose para ambiente local.
- CI, testes, logs centralizados, monitoramento e estratégia de deploy como evolução do projeto.

## Estrutura atual

```text
.
├── README.md
├── AGENTS.md
├── .github/workflows/
└── backend/
    ├── docker-compose.yml
    ├── init-postgres/
    ├── eureka-service/
    ├── gateway-service/
    ├── user-service/
    ├── product-catalog-service/
    └── notification-service/
```

## Estado atual conhecido

Implementado ou parcialmente implementado:
- `eureka-service`: servidor Eureka para service discovery.
- `gateway-service`: Spring Cloud Gateway com rotas para `user-service` e `product-catalog-service`.
- `user-service`: cadastro, login, refresh token, JWT/RSA, Spring Security, JPA, Flyway e testes.
- `product-catalog-service`: CRUD de categorias, produtos e inventario, JPA, Flyway e testes.
- `notification-service`: consumo RabbitMQ para eventos de cadastro de usuario e confirmacao de pedido, envio por console/Gmail, templates Thymeleaf e log em H2.
- `backend/docker-compose.yml`: sobe PostgreSQL, Eureka, Gateway, User, Product Catalog, RabbitMQ e Notification.
- CI inicial para `user-service` e `product-catalog-service`.

Ainda ausente ou incompleto:
- `shopping-cart-service`.
- `order-service`.
- `payment-service`.
- Integracao real entre `user-service` e `notification-service` via eventos RabbitMQ.
- Fluxo real de pedidos, pagamento, reserva/baixa de estoque e notificacao.
- Rotas do gateway para notificacao, pedido, carrinho e pagamento.
- Logging centralizado, tracing distribuido, metricas Prometheus/Grafana e dashboards.
- CI para `gateway-service`, `eureka-service` e `notification-service`.
- Deploy de producao com Kubernetes, Docker Swarm ou equivalente.
- Documentacao didatica consistente para estudar arquitetura, fluxos e decisoes.

## Comandos

Executar a stack local:

```powershell
cd backend
docker compose up --build
```

Executar testes por servico:

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

Observacao: nao existe Maven parent/agregador na raiz do backend. Rode comandos por servico.

## Padroes de trabalho

- Preserve a separacao por microservico. Evite acoplamento direto entre bancos de dados de servicos diferentes.
- Prefira comunicacao REST sincrona para consultas simples entre servicos e eventos RabbitMQ para efeitos colaterais assincronos.
- Mantenha cada servico com seu proprio banco, migrations Flyway, README e testes.
- Ao adicionar um novo servico, inclua:
  - `Dockerfile`
  - `README.md`
  - `pom.xml`
  - `src/main/resources/application.yml`
  - perfil local/dev se seguir o padrao existente
  - migrations Flyway quando houver persistencia relacional
  - testes de controller/service/repository conforme risco
  - entrada no `backend/docker-compose.yml`
  - rota no `gateway-service`
  - workflow de CI em `.github/workflows/`
- Use variaveis de ambiente para credenciais, URLs e chaves. Nao adicione segredos reais ao repositorio.
- Antes de alterar endpoints, confira se README, testes e gateway continuam coerentes.
- Se mexer em contrato entre servicos, documente o contrato em README ou em `docs/`.

## Inconsistencias a corrigir

- Os READMEs citam servicos e rotas ainda inexistentes, como `order-service`.
- `gateway-service` documenta rotas que nao estao configuradas.
- Banco local agora usa PostgreSQL via Docker Compose, com bancos `user_db` e `product_db`.
- Serviços padronizados para Java 25, Spring Boot 3.5.14 e Spring Cloud 2025.0.2.
- As chaves RSA em `user-service/src/main/resources/certs/` devem ser tratadas como material sensivel em ambientes reais.

## Proxima estrutura sugerida

```text
.
├── docs/
│   ├── 00-visao-geral.md
│   ├── 01-como-rodar-local.md
│   ├── 02-arquitetura.md
│   ├── 03-fluxos.md
│   ├── 04-servicos/
│   │   ├── user-service.md
│   │   ├── product-catalog-service.md
│   │   ├── notification-service.md
│   │   ├── shopping-cart-service.md
│   │   ├── order-service.md
│   │   └── payment-service.md
│   ├── 05-contratos-api.md
│   ├── 06-eventos-rabbitmq.md
│   ├── 07-bancos-e-migrations.md
│   ├── 08-testes.md
│   ├── 09-observabilidade.md
│   └── 10-roadmap.md
├── backend/
│   ├── docker-compose.yml
│   ├── services/
│   │   ├── eureka-service/
│   │   ├── gateway-service/
│   │   ├── user-service/
│   │   ├── product-catalog-service/
│   │   ├── notification-service/
│   │   ├── shopping-cart-service/
│   │   ├── order-service/
│   │   └── payment-service/
│   └── infra/
│       ├── postgres/
│       ├── rabbitmq/
│       ├── observability/
│       └── scripts/
```

Nao mova pastas para essa estrutura sem antes ajustar paths de Docker Compose, GitHub Actions, READMEs e comandos de build.
