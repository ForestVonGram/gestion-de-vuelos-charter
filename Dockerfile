# Build stage
FROM gradle:8.14-jdk21 AS builder
WORKDIR /app
COPY backend/build.gradle .
COPY backend/gradle ./gradle
COPY backend/src ./src
RUN gradle build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
