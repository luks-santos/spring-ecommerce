FROM eclipse-temurin:23.0.2_7-jdk-alpine

RUN apk add --no-cache bash

EXPOSE 8084
WORKDIR /root

COPY ./pom.xml /root
COPY ./.mvn /root/.mvn
COPY ./mvnw /root

RUN chmod +x mvnw && \
    sed -i 's/\r$//' mvnw

RUN ./mvnw dependency:go-offline

COPY ./src /root/src

RUN ./mvnw clean install

ENTRYPOINT ["java","-jar","/root/target/notification-service-0.0.1-SNAPSHOT.jar"]
