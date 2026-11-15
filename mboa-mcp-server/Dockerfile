# ---- Étape 1 : construction ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY mboa-common mboa-common
COPY mboa-mcp-server mboa-mcp-server
COPY mboa-orchestrator/pom.xml mboa-orchestrator/pom.xml
RUN mvn -B -q -pl mboa-mcp-server -am package -DskipTests

# ---- Étape 2 : exécution ----
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 1001 mboa
COPY --from=build /app/mboa-mcp-server/target/mboa-mcp-server-*.jar app.jar
USER mboa
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
