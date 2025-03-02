FROM eclipse-temurin:23.0.2_7-jdk-alpine

# Instala bash e dependências básicas
RUN apk add --no-cache bash

EXPOSE 8080
WORKDIR /root

# Copia os arquivos necessários
COPY ./pom.xml /root
COPY ./.mvn /root/.mvn
COPY ./mvnw /root

# Corrige permissões e quebras de linha
RUN chmod +x mvnw && \
    sed -i 's/\r$//' mvnw

# Baixa as dependências do Maven
RUN ./mvnw dependency:go-offline

# Copia o código-fonte
COPY ./src /root/src

# Compila e empacota o projeto
RUN ./mvnw clean install -Pprod

# Comando para iniciar a aplicação
ENTRYPOINT ["java","-jar","/root/target/user-service-0.0.1-SNAPSHOT.jar"]
