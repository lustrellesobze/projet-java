# ---- Étape 1 : construction ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY mboa-common mboa-common
COPY mboa-orchestrator mboa-orchestrator
COPY mboa-mcp-server/pom.xml mboa-mcp-server/pom.xml
RUN mvn -B -q -pl mboa-orchestrator -am package -DskipTests

# ---- Étape 2 : exécution ----
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 1001 mboa
COPY --from=build /app/mboa-orchestrator/target/mboa-orchestrator-*.jar app.jar
USER mboa
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
