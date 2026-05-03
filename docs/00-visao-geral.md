# Visao geral

Este projeto implementa uma plataforma de e-commerce baseada em microservicos,
seguindo o desafio `Scalable E-Commerce Platform` do roadmap.sh.

## Ideia principal

Uma loja online completa precisa lidar com usuarios, catalogo, estoque, carrinho,
pedidos, pagamento e notificacoes. Em uma arquitetura monolitica, tudo isso fica
no mesmo deploy e no mesmo limite de mudanca. Neste projeto, cada capacidade
principal deve evoluir como um microservico independente.

## Componentes atuais

- `gateway-service`: ponto de entrada HTTP da plataforma.
- `eureka-service`: registro e descoberta de servicos.
- `user-service`: cadastro, login, JWT, refresh token e perfil de usuario.
- `product-catalog-service`: categorias, produtos e inventario.
- `shopping-cart-service`: carrinho de compras por usuario.
- `order-service`: criacao de pedidos manualmente ou a partir do carrinho.
- `payment-service`: registro e confirmacao simulada de pagamentos.
- `notification-service`: notificacoes assincronas consumindo eventos RabbitMQ.
- `postgres-ecommerce`: banco PostgreSQL local com bancos separados por servico.
- `rabbitmq`: broker para comunicacao assincrona por eventos.

## Componentes planejados

- Endurecimento de autenticacao e autorizacao entre gateway e servicos.
- Observabilidade: logs centralizados, metricas e tracing distribuido.
- Deploy: empacotamento e execucao em ambiente mais proximo de producao.

## Decisao de banco

O projeto foi migrado de MySQL para PostgreSQL. A partir desta etapa:

- Compose local usa `postgres:16-alpine`.
- `user-service` usa o banco `user_db`.
- `product-catalog-service` usa o banco `product_db`.
- `shopping-cart-service` usa o banco `cart_db`.
- `order-service` usa o banco `order_db`.
- `payment-service` usa o banco `payment_db`.
- As migrations Flyway usam UUID nativo do PostgreSQL.
- Cada microservico deve continuar dono do proprio banco.
