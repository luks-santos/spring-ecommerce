FROM eclipse-temurin:25-jdk-alpine

RUN apk add --no-cache bash

EXPOSE 8086
WORKDIR /root

COPY ./pom.xml /root
COPY ./.mvn /root/.mvn
COPY ./mvnw /root

RUN chmod +x mvnw && \
    sed -i 's/\r$//' mvnw

RUN ./mvnw dependency:go-offline

COPY ./src /root/src

RUN ./mvnw clean package -Phomolog -DskipTests

ENTRYPOINT ["java","-jar","/root/target/payment-service-0.0.1-SNAPSHOT.jar"]
