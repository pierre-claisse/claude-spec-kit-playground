# Research: User CRUD API

**Date**: 2026-02-05
**Status**: Complete (all decisions provided by user)

## Technology Decisions

### Build Tool: Maven

**Decision**: Maven with spring-boot-starter-parent
**Rationale**: User-specified requirement
**Alternatives considered**: Gradle (not selected per user requirement)

### Framework: Spring Boot 3.5.10

**Decision**: Spring Boot 3.5.10 with Spring Web and Spring Data JPA
**Rationale**: User-specified requirement
**Alternatives considered**: None (explicit version requirement)

**Note**: Spring Boot 3.5.x requires Java 17+ (Java 21 LTS recommended)

### Database: PostgreSQL

**Decision**: PostgreSQL running exclusively in Docker containers
**Rationale**: User-specified non-negotiable requirement
**Alternatives considered**: H2/in-memory (explicitly rejected by user)

### Testing: Testcontainers

**Decision**: @SpringBootTest with Testcontainers for PostgreSQL
**Rationale**: User-specified requirement; tests must run in containers with real database
**Alternatives considered**: None (requirement is integration tests with real database)

### Runtime: Docker-Only

**Decision**: All execution (dev, test, prod) in Docker containers
**Rationale**: User-specified non-negotiable requirement
**Alternatives considered**: Local execution (explicitly rejected by user)

## Implementation Approach

### Architecture Pattern

Per constitution (Direct Architecture principle):
- No service layer (controller directly uses repository)
- No DTOs (entity used directly in responses)
- No mappers or transfer objects

### Dockerfile Strategy

Multi-stage build:
1. **Build stage**: Maven image, compile and package JAR
2. **Runtime stage**: Minimal JRE image, copy and run JAR

### Docker Compose Setup

Two containers:
1. **db**: PostgreSQL container with volume for data persistence
2. **app**: Application container depending on db, environment variables for connection

### Validation Approach

Per constitution (Stripped-Down Features principle):
- Minimal validation: only check required fields are non-null/non-empty
- Use Spring's built-in validation annotations (@NotBlank)
- No custom validators

### Error Handling

Per constitution (Stripped-Down Features principle):
- No custom exceptions
- Return standard Spring error responses
- Let framework handle 400/404/500 responses

## Dependencies

```xml
<!-- Required dependencies -->
spring-boot-starter-web        <!-- REST endpoints -->
spring-boot-starter-data-jpa   <!-- JPA/Hibernate -->
postgresql                     <!-- PostgreSQL JDBC driver -->
spring-boot-starter-test       <!-- Testing -->
testcontainers                 <!-- PostgreSQL container for tests -->
```

## Open Questions

None - all requirements clearly specified by user.
