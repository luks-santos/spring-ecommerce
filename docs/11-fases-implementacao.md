# Fases de Implementacao e Estimativas

Este documento organiza as proximas etapas de implementacao da plataforma de e-commerce, considerando o que ja existe no repositorio e o que ainda esta pendente.

As estimativas assumem uma pessoa desenvolvedora, conhecimento intermediario em Spring Boot e microservicos, com foco em implementacao, testes, Docker, CI e documentacao minima. Para converter horas em dias, foi considerada uma media de 6 horas produtivas por dia.

## Resumo Geral

| Fase | Objetivo | Estimativa |
| --- | --- | --- |
| 0 | Correcoes de base e alinhamento tecnico | 8-14h / 1-2 dias |
| 1 | Documentacao e contratos atuais | 8-16h / 1-3 dias |
| 2 | Integracao `user-service` -> `notification-service` via RabbitMQ | 10-18h / 2-3 dias |
| 3 | Implementacao do `shopping-cart-service` | 18-32h / 3-6 dias |
| 4 | Implementacao do `order-service` | 28-48h / 5-8 dias |
| 5 | Implementacao do `payment-service` | 20-36h / 4-6 dias |
| 6 | Fluxo completo de pedido, pagamento, estoque e notificacao | 24-48h / 4-8 dias |
| 7 | Gateway, Docker Compose e CI completos | 12-24h / 2-4 dias |
| 8 | Observabilidade inicial | 16-32h / 3-6 dias |
| 9 | Hardening, testes integrados e revisao final | 24-48h / 4-8 dias |

Estimativa total realista: **168-316h**, aproximadamente **28-53 dias uteis** de 6 horas produtivas.

## Fase 0: Correcoes de Base

Objetivo: deixar o projeto consistente antes de expandir a arquitetura.

Tarefas:

- Verificar os workflows de CI dos servicos e manter as branches `main` e `develop` consistentes.
- Alinhar o README do `gateway-service` com as rotas realmente configuradas.
- Confirmar que todos os servicos estao padronizados em Java 25, Spring Boot 3.5.x e Spring Cloud 2025.0.x.
- Revisar o `backend/docker-compose.yml`, nomes de servicos, bancos e variaveis de ambiente.

Estimativa: **8-14h / 1-2 dias**.

## Fase 1: Documentacao e Contratos

Objetivo: criar uma base didatica para guiar as proximas fases e reduzir inconsistencias entre implementacao, README e arquitetura planejada.

Tarefas:

- Completar a estrutura inicial de `docs/`.
- Escrever a visao geral da arquitetura.
- Documentar os servicos existentes.
- Documentar APIs atuais.
- Documentar eventos RabbitMQ planejados.
- Criar ou atualizar o roadmap tecnico.
- Registrar decisoes arquiteturais relevantes, como comunicacao REST para consultas simples e eventos RabbitMQ para efeitos colaterais assincronos.

Estimativa: **8-16h / 1-3 dias**.

## Fase 2: Integracao User + Notification

Objetivo: completar o primeiro fluxo assincrono real da plataforma.

Fluxo esperado:

1. Usuario e cadastrado no `user-service`.
2. `user-service` publica um evento RabbitMQ.
3. `notification-service` consome o evento.
4. `notification-service` envia ou registra a notificacao.

Tarefas:

- Publicar evento no `user-service` apos cadastro de usuario.
- Definir exchange, routing key e payload.
- Consumir o evento no `notification-service`.
- Documentar o contrato do evento sem criar dependencia direta entre os servicos.
- Adicionar testes de publisher e consumer quando viavel.
- Atualizar README dos dois servicos.

Estimativa: **10-18h / 2-3 dias**.

## Fase 3: Shopping Cart Service

Objetivo: implementar o servico independente de carrinho.

Escopo sugerido:

- Criar o `shopping-cart-service` com Spring Boot.
- Criar entidades `cart` e `cart_item`.
- Criar migrations Flyway.
- Criar banco proprio, por exemplo `cart_db`.
- Implementar endpoints para:
  - criar ou obter carrinho por usuario;
  - adicionar item;
  - alterar quantidade;
  - remover item;
  - limpar carrinho.
- Validar produtos via `product-catalog-service`.
- Adicionar `Dockerfile`.
- Adicionar entrada no `backend/docker-compose.yml`.
- Adicionar rota no `gateway-service`.
- Criar README do servico.
- Criar workflow de CI.
- Criar testes de controller, service e repository conforme risco.

Estimativa: **18-32h / 3-6 dias**.

## Fase 4: Order Service

Objetivo: permitir que um carrinho seja transformado em pedido.

Escopo sugerido:

- Criar o `order-service` com Spring Boot.
- Criar entidades `order`, `order_item` e `order_status`.
- Criar migrations Flyway.
- Criar banco proprio, por exemplo `order_db`.
- Implementar endpoint para criar pedido a partir do carrinho.
- Implementar consulta de pedidos por usuario.
- Implementar consulta de pedido por ID.
- Definir estados basicos:
  - `CREATED`;
  - `WAITING_PAYMENT`;
  - `PAID`;
  - `CANCELLED`;
  - `FAILED`.
- Integrar com `shopping-cart-service` e `product-catalog-service`.
- Publicar evento `order.created`.
- Adicionar `Dockerfile`, Compose, gateway, README e CI.
- Criar testes de controller, service e repository.

Estimativa: **28-48h / 5-8 dias**.

## Fase 5: Payment Service

Objetivo: implementar pagamentos de forma isolada, inicialmente com simulacao.

Escopo inicial recomendado:

- Criar o `payment-service` com Spring Boot.
- Usar pagamento simulado, sem gateway externo real.
- Criar entidades `payment`, `payment_status` e `payment_method`.
- Criar migrations Flyway.
- Criar banco proprio, por exemplo `payment_db`.
- Implementar endpoint para iniciar pagamento.
- Implementar endpoint para consultar pagamento.
- Simular aprovacao e rejeicao.
- Publicar eventos:
  - `payment.approved`;
  - `payment.rejected`.
- Adicionar `Dockerfile`, Compose, gateway, README e CI.
- Criar testes de controller, service e repository.

Estimativa: **20-36h / 4-6 dias**.

## Fase 6: Fluxo Completo

Objetivo: conectar pedido, pagamento, estoque e notificacao em um fluxo real de e-commerce.

Fluxo esperado:

1. Usuario cria ou atualiza carrinho.
2. Usuario cria pedido.
3. Pedido solicita ou aguarda pagamento.
4. Pagamento e aprovado ou rejeitado.
5. Pedido atualiza status.
6. Estoque e reservado ou baixado.
7. Notificacao e enviada.

Tarefas:

- Definir eventos RabbitMQ do fluxo de pedidos.
- Implementar listeners no `order-service`.
- Implementar reserva ou baixa de estoque no `product-catalog-service`.
- Definir estrategia simples de falha e compensacao.
- Atualizar pedido para `PAID`, `FAILED` ou `CANCELLED` conforme eventos de pagamento e estoque.
- Publicar evento para o `notification-service`.
- Testar o fluxo completo localmente via Docker Compose.
- Documentar o fluxo em `docs/`.

Estimativa: **24-48h / 4-8 dias**.

## Fase 7: Gateway, Docker Compose e CI

Objetivo: garantir que todos os servicos subam, sejam acessiveis e tenham validacao automatizada.

Tarefas:

- Adicionar rotas no gateway para:
  - `/api/cart/**`;
  - `/api/orders/**`;
  - `/api/payments/**`;
  - opcionalmente `/api/notifications/**`.
- Atualizar `backend/docker-compose.yml`.
- Criar bancos:
  - `cart_db`;
  - `order_db`;
  - `payment_db`.
- Criar workflows de CI para:
  - `gateway-service`;
  - `eureka-service`;
  - `notification-service`;
  - `shopping-cart-service`;
  - `order-service`;
  - `payment-service`.
- Atualizar comandos no README principal e nos READMEs dos servicos.

Estimativa: **12-24h / 2-4 dias**.

## Fase 8: Observabilidade Inicial

Objetivo: adicionar visibilidade basica sobre saude, metricas e logs dos servicos.

Escopo sugerido:

- Habilitar Spring Boot Actuator em todos os servicos.
- Adicionar health checks no Docker Compose.
- Padronizar logs estruturados basicos.
- Adicionar Prometheus via Docker Compose.
- Adicionar Grafana via Docker Compose.
- Expor metricas HTTP e JVM.
- Criar dashboard simples por servico.
- Documentar como acessar e interpretar metricas basicas.

Estimativa: **16-32h / 3-6 dias**.

## Fase 9: Hardening, Testes Integrados e Revisao Final

Objetivo: estabilizar o projeto como uma plataforma demonstravel e coerente.

Tarefas:

- Criar testes integrados com Testcontainers onde fizer sentido.
- Testar fluxo completo de pedido, pagamento, estoque e notificacao.
- Revisar seguranca do gateway.
- Revisar contratos entre servicos.
- Corrigir inconsistencias nos READMEs.
- Criar colecao HTTP com Bruno, Insomnia ou Postman.
- Revisar migrations e dados de seed.
- Validar `docker compose up --build` a partir de ambiente limpo.
- Revisar documentacao final.

Estimativa: **24-48h / 4-8 dias**.

## Ordem Recomendada

Ordem sugerida de execucao:

1. Fase 0: Correcoes de base.
2. Fase 1: Documentacao e contratos.
3. Fase 2: Integracao `user-service` + `notification-service`.
4. Fase 3: `shopping-cart-service`.
5. Fase 4: `order-service`.
6. Fase 5: `payment-service`.
7. Fase 6: fluxo completo.
8. Fase 7: gateway, Docker Compose e CI completos.
9. Fase 8: observabilidade inicial.
10. Fase 9: hardening, testes integrados e revisao final.

## Marco Critico

A fase mais critica e a **Fase 6**, porque e nela que o projeto deixa de ser uma colecao de servicos independentes e passa a ter um fluxo real de e-commerce.

Antes dessa fase, o foco e construir pecas bem isoladas. A partir dela, o foco passa a ser consistencia entre servicos, tratamento de falhas, eventos, status de pedido, estoque e notificacoes.
