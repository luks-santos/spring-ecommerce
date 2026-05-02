# Documentacao de estudo

Este diretorio sera a trilha didatica para entender e evoluir o projeto.

## Objetivo

Explicar o sistema em camadas, partindo da visao geral ate os detalhes de cada
microservico, sem assumir conhecimento previo profundo de arquitetura distribuida.

## Trilha sugerida

1. `00-visao-geral.md`
   - O que o projeto tenta construir.
   - Relacao com o roadmap.sh.
   - Quais problemas uma plataforma de e-commerce precisa resolver.

2. `01-como-rodar-local.md`
   - Pre-requisitos.
   - Variaveis de ambiente.
   - Como subir com Docker Compose.
   - Como rodar cada servico isoladamente.
   - Como executar testes.

3. `02-arquitetura.md`
   - Microservicos existentes.
   - API Gateway.
   - Eureka/service discovery.
   - Banco por servico.
   - Comunicacao sincrona e assincrona.

4. `03-fluxos.md`
   - Cadastro e login de usuario.
   - CRUD de catalogo.
   - Controle de inventario.
   - Notificacao por evento.
   - Fluxos futuros: carrinho, pedido e pagamento.

5. `04-servicos/`
   - Um arquivo por microservico.
   - Responsabilidades, endpoints, banco, testes e pontos pendentes.

6. `05-contratos-api.md`
   - Endpoints expostos pelo gateway.
   - Contratos REST internos e externos.
   - Padrao de respostas e erros.

7. `06-eventos-rabbitmq.md`
   - Exchanges, filas e routing keys.
   - Payloads dos eventos.
   - Quando usar eventos em vez de REST.

8. `07-bancos-e-migrations.md`
   - Modelo de dados por servico.
   - Migrations Flyway.
   - Regras para nao compartilhar banco entre servicos.

9. `08-testes.md`
   - Como os testes estao organizados.
   - O que falta cobrir.
   - Como testar contratos entre servicos.

10. `09-observabilidade.md`
    - Logs.
    - Metricas.
    - Tracing.
    - Health checks.

11. `10-roadmap.md`
    - Status atual.
    - Proximas entregas.
    - Ordem recomendada de implementacao.

## Ordem recomendada para escrever

1. Comecar por `00-visao-geral.md`, `01-como-rodar-local.md` e `10-roadmap.md`.
2. Documentar os servicos ja existentes: user, product catalog, notification, gateway e eureka.
3. Documentar contratos REST e eventos RabbitMQ.
4. So depois escrever os documentos dos servicos futuros: cart, order e payment.

## Criterio de qualidade

Cada documento deve responder:
- Qual problema esta parte resolve?
- Onde fica o codigo?
- Como roda?
- Como testa?
- Com quais outros servicos se comunica?
- O que esta pronto?
- O que ainda falta?

