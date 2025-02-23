# Pessoal Scalable E-Commerce Platform

Este é um projeto de e-commerce escalável baseado em arquitetura de microserviços, com o objetivo de criar uma plataforma robusta e eficiente para gerenciar uma loja online. O projeto é construído usando Docker, microserviços e outras tecnologias modernas para garantir escalabilidade, flexibilidade e alta disponibilidade.

O projeto segue o roadmap sugerido em [Roadmap para Plataforma de E-commerce Escalável](https://roadmap.sh/projects/scalable-ecommerce-platform), que descreve a criação de uma plataforma e-commerce usando microserviços. 

## Visão Geral

O objetivo do projeto é criar uma plataforma de e-commerce capaz de lidar com os principais aspectos de uma loja online, como gerenciamento de catálogo de produtos, autenticação de usuários, carrinho de compras, processamento de pagamentos e gerenciamento de pedidos. Cada uma dessas funcionalidades será implementada como um microserviço independente, permitindo o desenvolvimento, implantação e escalabilidade de forma isolada e eficiente.

### Principais Microserviços:
- **User Service**: Gerencia o cadastro, autenticação e perfil dos usuários.
- **Product Catalog Service**: Gerencia o catálogo de produtos, categorias e inventário.
- **Shopping Cart Service**: Gerencia os carrinhos de compras dos usuários.
- **Order Service**: Processa e gerencia os pedidos dos usuários.
- **Payment Service**: Processa os pagamentos, integrando com gateways de pagamento como Stripe ou PayPal.
- **Notification Service**: Envia notificações por e-mail ou SMS para eventos como confirmação de pedido e atualizações de envio.

### Componentes Adicionais:
- **API Gateway**: Roteia as requisições dos clientes para os microserviços apropriados.
- **Service Discovery**: Detecta automaticamente as instâncias de serviço e gerencia a comunicação entre eles.
- **Centralized Logging**: Agrega logs de todos os microserviços para facilitar o monitoramento e a depuração.
- **CI/CD Pipeline**: Automatiza a construção, testes e implantação de cada microserviço.
