# Implementation Plan: User CRUD API

**Branch**: `001-user-crud-api` | **Date**: 2026-02-05 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/001-user-crud-api/spec.md`

## Summary

Minimal REST API for CRUD operations on User resources using Spring Boot 3.5.10 with Spring Data JPA. All execution (development, testing, production) runs exclusively in Docker containers with PostgreSQL.

## Technical Context

**Language/Version**: Java 21 (LTS, required by Spring Boot 3.5.x)
**Primary Dependencies**: Spring Boot 3.5.10, Spring Web, Spring Data JPA, PostgreSQL Driver
**Storage**: PostgreSQL (containerized only)
**Testing**: @SpringBootTest with Testcontainers (integration tests only)
**Target Platform**: Docker containers (Linux)
**Project Type**: Single project
**Performance Goals**: N/A (not specified)
**Constraints**: All execution must be in Docker containers; no local/host execution
**Scale/Scope**: N/A (not specified)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. YAGNI-First | PASS | Only implementing requested CRUD endpoints |
| II. Extreme Minimalism | PASS | Direct controller-to-repository, no service layer |
| III. Stripped-Down Features | PASS | No validation beyond required fields, no extra logging |
| IV. Direct Architecture | PASS | No DTOs, no mappers, entity used directly |
| V. Testing Discipline | PASS | Integration tests only as requested |
| Technical Constraints | OVERRIDE | User explicitly requires PostgreSQL in Docker instead of in-memory DB |

**Override Justification**: User explicitly stated "PostgreSQL only, always running in a Docker container (no H2, no embedded database, no local database allowed)" as non-negotiable requirement. This overrides constitution's default in-memory database for development.

## Project Structure

### Documentation (this feature)

```text
specs/001-user-crud-api/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   └── openapi.yaml
└── tasks.md             # Phase 2 output (/speckit.tasks)
```

### Source Code (repository root)

```text
src/main/java/com/example/usercrud/
├── UserCrudApplication.java     # Spring Boot main class
├── User.java                    # JPA entity
├── UserRepository.java          # Spring Data JPA repository interface
└── UserController.java          # REST controller

src/main/resources/
└── application.properties       # Database connection config

src/test/java/com/example/usercrud/
└── UserControllerIntegrationTest.java  # Integration tests with Testcontainers

pom.xml                          # Maven build file
Dockerfile                       # Multi-stage build
docker-compose.yml               # App + PostgreSQL orchestration
```

**Structure Decision**: Single project with standard Maven layout. No service layer per constitution (direct repository-to-controller). Entity used directly in controller responses per constitution (no DTOs).

## Files to Create

| File | Responsibility |
|------|----------------|
| `pom.xml` | Maven build configuration with Spring Boot 3.5.10 parent, dependencies |
| `Dockerfile` | Multi-stage build: Maven build stage, minimal runtime stage with JAR |
| `docker-compose.yml` | Orchestrates app container and PostgreSQL container |
| `src/main/java/.../UserCrudApplication.java` | Spring Boot application entry point |
| `src/main/java/.../User.java` | JPA entity with id, name, email fields |
| `src/main/java/.../UserRepository.java` | Spring Data JPA repository interface |
| `src/main/java/.../UserController.java` | REST controller with 5 CRUD endpoints |
| `src/main/resources/application.properties` | Database URL, credentials (from env vars) |
| `src/test/java/.../UserControllerIntegrationTest.java` | Integration tests using Testcontainers |

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| PostgreSQL instead of H2 | User explicit requirement | User stated "no H2, no embedded database" as non-negotiable |
| Docker for all execution | User explicit requirement | User stated "never run directly on local host" as non-negotiable |
