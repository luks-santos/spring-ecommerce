# User Service

O serviço de usuários é um microserviço responsável por gerenciar operações relacionadas a usuários na Plataforma E-Commerce Escalável. Ele trata do cadastro, autenticação, gerenciamento de perfis e outras funções essenciais de usuários.

## Funcionalidades Principais

- Cadastro e criação de perfis de usuários  
- Autenticação e autorização de usuários
- Persistência de dados utilizando SQL

## Stack
- Java 23
- Spring Boot 3.3.9
- Maven
- MySQL 8.0


## Estrutura do Projeto

O serviço possui uma estrutura modular que facilita o desenvolvimento, teste e implantação. Diretórios principais:

- `/src/main/java`: Código-fonte principal da aplicação  
- `/src/main/resources`: Arquivos de configuração e recursos  
- `/src/test`: Testes unitários e de integração

## Executando o Serviço Localmente

1. Certifique-se de ter o Java e o Maven instalados.  
2. Navegue até o diretório `/user-service`.  
3. Construa o projeto com Maven:
   ```sh
    mvn clean package
   ```
   
4. Execute o serviço com Spring Boot:
   ```sh
    mvn spring-boot:run
   ```
O serviço iniciará na porta padrão (por exemplo, `8080`), que pode ser configurada no arquivo `application.properties`.

## Documentação da API

A documentação da API está disponível em `http://localhost:8080/swagger-ui.html` após a inicialização do serviço. Ela contém detalhes sobre os endpoints, parâmetros, respostas e exemplos de uso.