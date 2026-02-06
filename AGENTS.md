# AGENTS.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

Charter Flight Management System (Sistema de gestión de vuelos charter) - A Spring Boot backend application for managing charter flights.

## Tech Stack

- **Backend**: Spring Boot 4.0.2 with Java 21
- **Build**: Gradle
- **Databases**: PostgreSQL (JPA) + MongoDB
- **Auth**: Spring Security with OAuth2 Resource Server
- **Utilities**: Lombok, Bean Validation

## Common Commands

### Build & Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Clean build
./gradlew clean build
```

### Testing
```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.paeldav.backend.SomeTestClass"

# Run a specific test method
./gradlew test --tests "com.paeldav.backend.SomeTestClass.someMethod"
```

## Architecture

### Package Structure
Base package: `com.paeldav.backend`

Recommended layered architecture:
- `controller/` - REST API endpoints
- `service/` - Business logic
- `repository/` - Data access (JPA for PostgreSQL, MongoRepository for MongoDB)
- `model/` or `entity/` - Domain entities
- `dto/` - Data transfer objects
- `config/` - Configuration classes (Security, Database, etc.)

### Database Configuration
The project uses dual databases:
- **PostgreSQL** via Spring Data JPA for relational data
- **MongoDB** via Spring Data MongoDB for document-based data

Configure connections in `src/main/resources/application.properties` or use profile-specific files.

### Security
OAuth2 Resource Server is configured. JWT tokens should be validated against an authorization server.
