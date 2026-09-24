# ===================================================================
# Multi-Stage Dockerfile for DharwinMart (Spring Boot 3 + Java 17)
# Optimized for Cloud Deployment on Render
# ===================================================================

# -------------------------------------------------------------------
# Stage 1: Build Application
# -------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /build

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code and compile jar
COPY src ./src
RUN mvn clean package -DskipTests

# -------------------------------------------------------------------
# Stage 2: Minimal Runtime Environment
# -------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create directory for embedded H2 database persistence
RUN mkdir -p /app/data

# Copy packaged jar from builder
COPY --from=builder /build/target/*.jar app.jar

# Render injects PORT environment variable dynamically (defaults to 8080 if running locally)
ENV PORT=8080
EXPOSE ${PORT}

# Run with memory constraints optimized for Render free/starter tier (512MB RAM)
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT:-8080} -jar app.jar"]
