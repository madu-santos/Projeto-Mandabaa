# ============================================================
# Dockerfile — Mandabaa (Spring Boot 3.3.5 / Java 21)
# Build em duas fases: 1) compila o JAR  2) imagem final leve
# ============================================================

# ---- FASE 1: build Maven ----
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copia só o pom.xml primeiro para aproveitar cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copia o código-fonte e compila (pula os testes — já comprovados em 51/51)
COPY src ./src
RUN mvn package -DskipTests -q

# ---- FASE 2: imagem de execução ----
FROM eclipse-temurin:21-jre-alpine

LABEL org.opencontainers.image.title="Mandabaa"
LABEL org.opencontainers.image.description="Plataforma de Gestão e Incentivo à Reciclagem de PET"

WORKDIR /app

# Copia apenas o JAR gerado
COPY --from=build /app/target/projeto-mandabaa-0.0.1-SNAPSHOT.jar mandabaa.jar

# Porta exposta pela aplicação Spring Boot
EXPOSE 8080

# Variáveis de ambiente com defaults (sobrescritas pelo docker-compose)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-mandabaa:5432/mandabaa_db
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgrespassword
ENV JAVA_OPTS="-Xmx256m -Xms128m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar mandabaa.jar"]
