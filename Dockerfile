# Build stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY backend/ .
RUN gradle build -x test

# Runtime stage
FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/build/libs/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
