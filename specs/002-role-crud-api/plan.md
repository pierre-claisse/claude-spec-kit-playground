# Implementation Plan: Role CRUD API

**Branch**: `002-role-crud-api` | **Date**: 2026-02-05 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/002-role-crud-api/spec.md`

## Summary

Extend the existing User CRUD API with Role resources and a many-to-many relationship. Adds 5 Role CRUD endpoints and 3 User-Role management endpoints. Uses existing Spring Boot 3.5.10 with Spring Data JPA, PostgreSQL in Docker containers only.

## Technical Context

**Language/Version**: Java 21 (LTS, required by Spring Boot 3.5.x)
**Primary Dependencies**: Spring Boot 3.5.10, Spring Web, Spring Data JPA, PostgreSQL Driver
**Storage**: PostgreSQL (containerized only)
**Testing**: @SpringBootTest with Testcontainers (integration tests only)
**Target Platform**: Docker containers (Linux)
**Project Type**: Single project (extending existing)
**Performance Goals**: N/A (not specified)
**Constraints**: All execution must be in Docker containers; no local/host execution
**Scale/Scope**: N/A (not specified)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. YAGNI-First | PASS | Only implementing requested Role CRUD and user-role endpoints |
| II. Extreme Minimalism | PASS | Direct controller-to-repository, no service layer |
| III. Stripped-Down Features | PASS | No extra validation, logging, or features beyond spec |
| IV. Direct Architecture | PASS | No DTOs, no mappers, entity used directly |
| V. Testing Discipline | PASS | Integration tests only as requested |
| Technical Constraints | OVERRIDE | User explicitly requires PostgreSQL in Docker instead of in-memory DB |

**Override Justification**: User explicitly stated "PostgreSQL only, always running in a Docker container (no H2, no embedded database, no local database allowed)" as non-negotiable requirement. This overrides constitution's default in-memory database for development.

## Project Structure

### Documentation (this feature)

```text
specs/002-role-crud-api/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   └── openapi.yaml
├── diagrams/            # PlantUML source files
│   ├── data-model.puml
│   ├── use-cases.puml
│   ├── sequence-create-role.puml
│   ├── sequence-list-roles.puml
│   ├── sequence-get-role.puml
│   ├── sequence-update-role.puml
│   ├── sequence-delete-role.puml
│   ├── sequence-list-user-roles.puml
│   ├── sequence-add-role-to-user.puml
│   └── sequence-remove-role-from-user.puml
└── tasks.md             # Phase 2 output (/speckit.tasks)
```

### Source Code (repository root)

```text
src/main/java/com/example/usercrud/
├── UserCrudApplication.java     # Existing (no changes)
├── User.java                    # MODIFIED: add @ManyToMany roles field
├── UserRepository.java          # Existing (no changes)
├── UserController.java          # MODIFIED: add user-role endpoints
├── Role.java                    # NEW: Role JPA entity
├── RoleRepository.java          # NEW: Spring Data JPA repository
└── RoleController.java          # NEW: Role CRUD REST controller

src/main/resources/
└── application.properties       # Existing (no changes)

src/test/java/com/example/usercrud/
├── UserControllerIntegrationTest.java      # Existing (no changes)
└── RoleControllerIntegrationTest.java      # NEW: Role integration tests

pom.xml                          # Existing (no changes needed)
Dockerfile                       # Existing (no changes needed)
docker-compose.yml               # Existing (no changes needed)
docker-compose.test.yml          # Existing (no changes needed)
```

**Structure Decision**: Extending existing single project with standard Maven layout. Adding Role entity and RoleController for Role CRUD. Extending UserController for user-role management endpoints. No service layer per constitution (direct repository-to-controller).

## Files to Create/Modify

| File | Status | Responsibility |
|------|--------|----------------|
| `src/.../User.java` | MODIFY | Add `@ManyToMany` relationship to Role, getter/setter for roles |
| `src/.../Role.java` | NEW | JPA entity with id, name (unique), inverse side of relationship |
| `src/.../RoleRepository.java` | NEW | Spring Data JPA repository with custom queries for uniqueness checks |
| `src/.../RoleController.java` | NEW | REST controller for Role CRUD (POST/GET/PUT/DELETE on /roles) |
| `src/.../UserController.java` | MODIFY | Add user-role endpoints (GET/POST/DELETE on /users/{id}/roles) |
| `src/test/.../RoleControllerIntegrationTest.java` | NEW | Integration tests for all 8 new endpoints |

## New Endpoints

### Role CRUD (RoleController)

| Method | Path | Description |
|--------|------|-------------|
| POST | /roles | Create role (201, 400, 409) |
| GET | /roles | List all roles (200) |
| GET | /roles/{id} | Get role by ID (200, 404) |
| PUT | /roles/{id} | Update role (200, 400, 404, 409) |
| DELETE | /roles/{id} | Delete role (204, 404, 409) |

### User-Role Management (UserController)

| Method | Path | Description |
|--------|------|-------------|
| GET | /users/{userId}/roles | List user's roles (200, 404) |
| POST | /users/{userId}/roles/{roleId} | Assign role to user (200, 404, 409) |
| DELETE | /users/{userId}/roles/{roleId} | Remove role from user (204, 404) |

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| PostgreSQL instead of H2 | User explicit requirement | User stated "no H2, no embedded database" as non-negotiable |
| Docker for all execution | User explicit requirement | User stated "never run directly on local host" as non-negotiable |

## Test Coverage

Integration tests will verify:
- Role CRUD operations (create, read, update, delete)
- Role name uniqueness constraint (409 on duplicate)
- Delete role blocked when assigned to users (409)
- User-role assignment and removal
- Edge cases (404 for non-existent IDs, 409 for duplicate assignments)
