# Roadmap do projeto

## Pronto ou parcialmente pronto

- Service discovery com `eureka-service`.
- API Gateway com rotas para usuarios, catalogo, carrinho, pedidos e pagamentos.
- `user-service` com autenticacao JWT/RSA, cadastro, login e refresh token.
- `user-service` publicando evento de cadastro de usuario para RabbitMQ.
- `product-catalog-service` com categorias, produtos e inventario.
- `shopping-cart-service` com persistencia de carrinho por usuario.
- `order-service` com criacao de pedido manual e criacao a partir do carrinho.
- `order-service` consumindo eventos de pagamento e publicando confirmacao de
  pedido.
- `payment-service` com fluxo simulado de criacao e confirmacao de pagamento.
- `notification-service` consumindo eventos RabbitMQ e renderizando emails.
- Docker Compose para ambiente local.
- PostgreSQL local em substituicao ao MySQL.
- CI por servico para a base atual do MVP.

## Lacunas tecnicas

- Falta endurecer autenticacao e autorizacao entre gateway, clientes e servicos.
- Falta remover confianca em `userId` enviado pelo cliente em fluxos privados.
- Falta padronizar propagacao de identidade em chamadas REST internas.
- Falta idempotencia e estrategia de compensacao nos fluxos de pedido, estoque e
  pagamento.
- Falta ampliar testes de integracao entre servicos.
- Falta observabilidade: logs centralizados, metricas e tracing.
- Falta estrategia de deploy mais proxima de producao.

## Ordem recomendada

1. Validar a stack completa com Docker Compose em ambiente com Java 25 e Docker.
2. Endurecer autenticacao e autorizacao, conforme `14-debito-autenticacao.md`.
3. Adicionar testes de integracao cobrindo carrinho, pedido, pagamento, estoque
   e notificacao.
4. Implementar idempotencia e compensacao nos fluxos de pedido e pagamento.
5. Adicionar observabilidade: logs centralizados, metricas e tracing.
6. Evoluir deploy para ambiente mais proximo de producao.
