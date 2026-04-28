# ── Stage 1: Build ────────────────────────────────────────
# Usa Maven para compilar y empaquetar el JAR
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copia primero solo el pom.xml para aprovechar el cache de Docker
# Si el pom no cambia, no re-descarga dependencias en cada build
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ahora copia el código fuente y compila
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Runtime ──────────────────────────────────────
# Imagen final liviana, solo necesita Java para correr el JAR
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia solo el JAR generado en el stage anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto que expone el backend
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]