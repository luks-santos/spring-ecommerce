# Roadmap do projeto

## Pronto ou parcialmente pronto

- Service discovery com `eureka-service`.
- API Gateway com rotas para `user-service` e `product-catalog-service`.
- `user-service` com autenticacao JWT/RSA, cadastro, login e refresh token.
- `product-catalog-service` com categorias, produtos e inventario.
- `notification-service` consumindo eventos RabbitMQ e renderizando emails.
- Docker Compose para ambiente local.
- PostgreSQL local em substituicao ao MySQL.
- CI inicial para `user-service` e `product-catalog-service`.

## Lacunas tecnicas

- Falta publicar evento real de cadastro de usuario para RabbitMQ.
- Falta implementar `shopping-cart-service`.
- Falta implementar `order-service`.
- Falta implementar `payment-service`.
- Falta gateway para carrinho, pedido, pagamento e possivelmente notificacao.
- Falta padronizar versoes Java/Spring Boot entre servicos.
- Falta CI para `gateway-service`, `eureka-service` e `notification-service`.
- Falta observabilidade: logs centralizados, metricas e tracing.

## Ordem recomendada

1. Validar a stack PostgreSQL com Docker Compose em execucao.
2. Corrigir documentacao antiga que cita servicos inexistentes como se estivessem prontos.
3. Integrar `user-service` com RabbitMQ para publicar evento de cadastro.
4. Criar `shopping-cart-service`.
5. Criar `order-service` integrando usuario, catalogo/inventario e notificacao.
6. Criar `payment-service`.
7. Adicionar observabilidade e CI faltante.
