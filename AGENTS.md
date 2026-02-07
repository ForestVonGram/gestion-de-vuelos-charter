# AGENTS.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

**Charter Flight Management System** (Sistema de gestión integral de vuelos chárter) - Full-stack application for managing charter flights, fleet maintenance, and crew administration.

## Tech Stack

**Backend** (`backend/`)
- Spring Boot 4.0.2 with Java 21
- Gradle build system
- PostgreSQL (JPA) + MongoDB (dual database)
- Spring Security with OAuth2 Resource Server
- MapStruct for DTO mapping, Lombok, Bean Validation
- Cloudinary integration (image uploads)
- WebSocket support

**Frontend** (`frontend-files/frontend/`)
- Angular 21 with TypeScript 5.9
- TailwindCSS 4 for styling
- Vitest for testing

## Common Commands

### Backend (run from `backend/` directory)
```bash
./gradlew build              # Build
./gradlew bootRun            # Run application
./gradlew clean build        # Clean build
./gradlew test               # Run all tests (JUnit 5)
./gradlew test --tests "com.paeldav.backend.SomeTestClass"           # Specific test class
./gradlew test --tests "com.paeldav.backend.SomeTestClass.someMethod" # Specific method
```

### Frontend (run from `frontend-files/frontend/` directory)
```bash
npm start           # Dev server (ng serve)
npm run build       # Production build
npm test            # Run tests (Vitest)
npm run watch       # Build with watch mode
```

## Architecture

### Backend - Clean Architecture
Base package: `com.paeldav.backend`

```
backend/src/main/java/com/paeldav/backend/
├── application/
│   ├── dto/           # DTOs organized by domain (vuelo/, aeronave/, etc.)
│   │                  # Pattern: {Entity}DTO, {Entity}CreateDTO, {Entity}UpdateDTO
│   ├── mapper/        # MapStruct mappers (Spring component model)
│   └── service/
│       ├── base/      # Service interfaces
│       ├── impl/      # Service implementations
│       └── integration/  # External services (CloudinaryService, EmailService)
├── domain/
│   ├── entity/        # JPA entities with Lombok
│   └── enums/         # EstadoVuelo, RolUsuario, TipoMantenimiento, etc.
├── exception/         # Custom exceptions
├── infraestructure/   # (note: Spanish spelling)
│   ├── config/        # SecurityConfig, CorsConfig, WebSocketConfig, etc.
│   ├── repository/    # Spring Data JPA repositories
│   └── security/      # Security-related classes
└── presentation/
    └── controller/    # REST controllers
```

### Domain Entities
- **Vuelo**: Flight requests with scheduling, aircraft/crew assignment, status tracking
- **Aeronave**: Fleet aircraft with technical specs and status
- **Tripulante**: Crew members assigned to flights
- **Personal**: Staff/personnel records
- **Usuario**: System users with roles
- **Mantenimiento**: Aircraft maintenance records
- **Repostaje**: Refueling records
- **Incidencia**: Incident reports
- **RegistroHorasVuelo**: Flight hours tracking
- **PasajeroVuelo**: Passenger-flight relationship

### Key Enums
- `EstadoVuelo`: SOLICITADO, PROGRAMADO, EN_VUELO, COMPLETADO, CANCELADO
- `RolUsuario`: User roles for access control
- `TipoMantenimiento`: PREVENTIVO, CORRECTIVO
- `EstadoAeronave`, `EstadoPersonal`, `EstadoTripulante`, `CargoPersonal`

### MapStruct Pattern
Mappers use `@Mapper(componentModel = "spring")` with `nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE`. Each mapper provides:
- `toDTO(Entity)` / `toDTOList(List<Entity>)`
- `toEntity(CreateDTO)`
- `updateEntityFromDTO(UpdateDTO, @MappingTarget Entity)`

### Database
Configure in `backend/src/main/resources/application.properties`:
- PostgreSQL for relational data (primary)
- MongoDB for document storage
