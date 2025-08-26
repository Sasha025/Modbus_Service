# Этап 1: сборка приложения
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

# Этап 2: запуск приложения
FROM eclipse-temurin:21-jre-jammy
ARG JAR_FILE=/app/target/demo-0.0.1-SNAPSHOT.jar
COPY --from=builder ${JAR_FILE} /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
