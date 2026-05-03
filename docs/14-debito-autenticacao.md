# Debito tecnico: autenticacao e autorizacao

## Status

Este projeto tem autenticacao suficiente para estudo do fluxo MVP, mas ainda nao
tem um modelo completo de seguranca entre gateway, clientes, microservicos e
eventos.

A decisao atual e manter essa limitacao documentada e evoluir a implementacao em
uma etapa propria, sem misturar a entrega do fluxo carrinho, pedido, pagamento e
notificacao com uma arquitetura de seguranca mais robusta.

## Estado atual

- `user-service` centraliza cadastro, login, refresh token e emissao de JWT com
  chaves RSA.
- O cliente obtem token nos endpoints de autenticacao do `user-service`.
- `gateway-service` roteia chamadas para os servicos, mas ainda nao atua como
  ponto central de validacao de JWT para todas as rotas privadas.
- `product-catalog-service`, `shopping-cart-service`, `order-service` e
  `payment-service` ainda nao estao padronizados como resource servers OAuth2.
- Alguns endpoints usam `userId` ou `userEmail` informados no path ou no corpo
  da requisicao, em vez de derivar a identidade diretamente do token autenticado.
- Chamadas internas REST entre servicos usam URLs internas e nao propagam uma
  identidade autenticada padronizada.
- Eventos RabbitMQ carregam dados de negocio, mas ainda nao possuem uma politica
  completa de confianca, assinatura, idempotencia e rastreabilidade.

## Riscos aceitos no MVP

- Um cliente indevido poderia tentar operar recursos de outro usuario se chamar
  endpoints expostos com outro `userId`.
- Chamadas diretas aos servicos podem contornar regras que deveriam existir no
  gateway, caso esses servicos sejam expostos fora da rede interna.
- A autorizacao por papel, escopo ou propriedade do recurso ainda nao esta
  modelada de forma consistente.
- Eventos podem ser processados sem uma validacao forte de origem ou duplicidade.
- O fluxo atual demonstra arquitetura distribuida, mas nao deve ser tratado como
  desenho de seguranca pronto para producao.

## Modelo alvo recomendado

1. `user-service` continua sendo o emissor dos tokens.
2. `gateway-service` passa a validar JWT em todas as rotas privadas.
3. Servicos de negocio tambem validam JWT quando puderem ser acessados
   diretamente.
4. Endpoints publicos deixam de receber `userId` como parametro de confianca e
   passam a derivar o usuario autenticado do token.
5. Claims estaveis no JWT devem incluir, no minimo, `sub`, `userId`, `email`,
   papeis ou escopos e expiracao curta.
6. Regras de autorizacao devem ficar explicitas por rota:
   - usuario comum acessa apenas seus recursos;
   - administradores gerenciam catalogo;
   - operacoes internas usam credenciais proprias.
7. Comunicacao servico-para-servico deve evoluir para uma destas opcoes:
   - OAuth2 client credentials para chamadas internas;
   - mTLS entre servicos;
   - rede interna fechada combinada com validacao no gateway para o ambiente de
     estudo.
8. RabbitMQ deve usar credenciais por servico, permissoes minimas por fila e
   eventos com `eventId`, `correlationId`, produtor, data de criacao e regra de
   idempotencia no consumidor.

## Criterios para fechar esta divida

- Gateway rejeita requisicoes sem JWT valido nas rotas privadas.
- Carrinho, pedido e pagamento usam o usuario autenticado do token, nao um
  `userId` arbitrario enviado pelo cliente.
- Um usuario nao consegue consultar ou alterar recursos de outro usuario.
- Servicos expostos diretamente validam JWT ou ficam inacessiveis fora da rede
  interna.
- Rotas administrativas exigem papel ou escopo administrativo.
- Eventos criticos possuem identificador unico e consumidores idempotentes.
- Testes cobrem autenticacao ausente, token invalido, acesso cruzado entre
  usuarios e permissao administrativa.
- Documentacao e colecao Postman refletem o novo fluxo autenticado.

## Ordem sugerida de implementacao

1. Documentar claims esperadas no JWT e padrao de autorizacao por rota.
2. Configurar `gateway-service` como resource server.
3. Proteger rotas privadas no gateway.
4. Alterar carrinho, pedido e pagamento para derivar usuario do token.
5. Adicionar testes de seguranca por servico e no gateway.
6. Revisar comunicacao interna e eventos RabbitMQ.
