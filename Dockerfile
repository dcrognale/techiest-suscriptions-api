# ─────────────────────────────────────────
# Stage 1: Build
# ─────────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# 1. Copy Gradle wrapper files FIRST (enables dependency caching)
COPY gradle/ gradle/
COPY gradlew .
COPY gradlew.bat .

# 2. Ensure gradlew is executable and wrapper jar exists
RUN chmod +x gradlew

# 3. Copy build definition files (cache layer — only invalidated on dependency changes)
COPY build.gradle settings.gradle ./

# 4. Download dependencies only (no source needed yet)
RUN ./gradlew dependencies --no-daemon --stacktrace

# 5. Copy source code
COPY src/ src/

# 6. Build the fat JAR, skip tests for faster build
RUN ./gradlew bootJar --no-daemon -x test --stacktrace

# ─────────────────────────────────────────
# Stage 2: Runtime
# ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy only the final JAR from the build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Railway injects PORT env var — Spring Boot reads it via SERVER_PORT
ENV SERVER_PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]