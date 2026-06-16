# Multi-stage build: Maven compile + lightweight JRE runtime
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache wget

RUN addgroup -S psymed && adduser -S psymed -G psymed
USER psymed

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENV SERVER_PORT=8080 \
    SPRING_JPA_SHOW_SQL=false \
    SPRING_JPA_DDL_AUTO=update

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
