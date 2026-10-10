# syntax=docker/dockerfile:1
# -----------------------------------------------------------------------------
# Stage 1: Build Application
# -----------------------------------------------------------------------------
FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder

WORKDIR /workspace

# Copy root and module POMs to leverage Docker layer caching for dependencies
COPY pom.xml ./
COPY tara-common/pom.xml tara-common/
COPY tara-server/pom.xml tara-server/

# Download dependencies in offline mode (cache layer)
RUN mvn dependency:go-offline -B

# Copy all source files
COPY tara-common/src tara-common/src
COPY tara-server/src tara-server/src

# Package production executable jar without running tests (tests run in CI stage)
RUN mvn clean package -DskipTests -B

# -----------------------------------------------------------------------------
# Stage 2: Minimal Production JRE Runtime
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runner

# Create non-root service user and group
RUN addgroup -S tara && adduser -S tara -G tara

WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /workspace/tara-server/target/tara-server-1.0.0.jar app.jar

# Adjust permissions
RUN chown -R tara:tara /app

USER tara

EXPOSE 8080

# Configure production container-aware JVM settings
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
