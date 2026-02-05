# Commands

```
/speckit.constitution Project constraints and principles (strict, non-negotiable):

- YAGNI above all: implement ONLY what is explicitly requested in the current task. Never add features, patterns, or abstractions "for the future" or "just in case".
- Extreme minimalism: the simplest possible code that works. No unnecessary layering, abstractions, or architectural patterns.
- No performance optimizations, no caching, no pagination, no custom query optimizations.
- No accessibility concerns, no i18n, no localization.
- No advanced security features unless explicitly requested.
- No input validation beyond the absolute minimum required for basic functionality.
- No custom exceptions or global exception handling unless explicitly requested.
- No additional logging beyond framework defaults.
- No DTOs, mappers, or transfer objects unless explicitly requested.
- Avoid separate service layers if direct repository-to-controller interaction suffices.
- Keep all components (controllers, services, repositories, entities) as small and direct as possible.
- Use in-memory database for development; containerized database only for integration tests when required.
- Tests: only basic integration tests when requested. No unit tests unless explicitly asked.
- Code style: stick strictly to framework defaults and conventions. No additional formatting rules, patterns, or enforced "best practices".
- Never suggest, propose, or add anything that increases complexity, even if it is considered a "best practice".

All responses must respect these rules without exception. No explanations, no justifications, no deviations.
```

```
/speckit.specify Create a minimal backend application with a REST API for basic CRUD operations on User resources.

A User has:
- An auto-generated unique ID
- A name (required string)
- An email (required string)

Required endpoints (JSON API):
- POST /users → create a new user (request body: name and email)
- GET /users → return list of all users
- GET /users/{id} → return single user by ID
- PUT /users/{id} → update user by ID (request body: name and/or email)
- DELETE /users/{id} → delete user by ID

Return appropriate HTTP status codes and JSON responses (user data on success, error details on failure).

Include basic integration tests that start the full application with a real database and verify each endpoint works end-to-end with valid and invalid inputs.

No authentication, no pagination, no filtering, no additional features.
```

```
/speckit.plan
Plan the implementation using the following technical choices:

- Build tool: Maven (standard pom.xml with spring-boot-starter-parent)
- Framework: Spring Boot version 3.5.10
- Persistence: Spring Data JPA with Hibernate
- Database: PostgreSQL only, always running in a Docker container (no H2, no embedded database, no local database allowed)
- Testing: @SpringBootTest with Testcontainers for integration tests only
- Runtime: ABSOLUTE AND NON-NEGOTIABLE REQUIREMENT — All code execution (development, testing, production, any environment) MUST run exclusively inside Docker containers. Never run the application, tests, or database directly on the local host machine. No local execution instructions allowed.

Mandatory deliverables:
- Dockerfile (multi-stage: build with Maven, run the JAR)
- docker-compose.yml to orchestrate the application container and a PostgreSQL container for development and manual testing

Produce a clear project plan that includes:

- Complete list of files and packages to create (including Dockerfile and docker-compose.yml)
- Brief description of each component's responsibility
- Full PlantUML source code for the following diagrams:
  - Data model (class diagram showing the User entity and its fields)
  - Use case diagram (actor interacting with the five CRUD use cases)
  - Sequence diagrams, one for each CRUD operation (POST create, GET list, GET by id, PUT update, DELETE)

Respect the constitution constraints strictly: keep everything extremely minimal, no extra layers, no additional features, no hot-reload, no devtools, no unnecessary configuration.
```

After this, run tasks, analyse and implement commands.

```
/speckit.specify Extend the existing REST API to add Role resources with full CRUD operations and a many-to-many relationship with User.

A Role has:
- An auto-generated unique ID
- A name (required string, must be unique)

Required endpoints for Roles (JSON API):
- POST /roles → create a new role (request body: name)
- GET /roles → return list of all roles
- GET /roles/{id} → return single role by ID
- PUT /roles/{id} → update role name by ID (request body: name)
- DELETE /roles/{id} → delete role by ID only if it is not assigned to any user; if assigned, return 409 Conflict with error message

Required endpoints for managing roles on a User (all actions initiated from the User side):
- GET /users/{userId}/roles → return list of roles assigned to the user
- POST /users/{userId}/roles/{roleId} → add the specified role to the user (return 404 if user or role not found, 409 if already assigned)
- DELETE /users/{userId}/roles/{roleId} → remove the specified role from the user (return 404 if user or role not found or if not assigned)

Return appropriate HTTP status codes and JSON responses (role/user data on success, error details on failure).

Include basic integration tests that verify:
- All Role CRUD operations, including successful delete and failed delete when role is assigned
- Assigning/removing roles to/from a user via the sub-resource endpoints
- That role uniqueness is enforced
- End-to-end behavior with valid and invalid inputs

No authentication, no pagination, no filtering, no bulk operations, no additional features.
```

```
/speckit.plan Plan the extension of the existing application to include Role resources and the many-to-many relationship with User.

Use the exact same technical choices as before:
- Build tool: Maven (standard pom.xml with spring-boot-starter-parent)
- Framework: Spring Boot version 3.5.10
- Persistence: Spring Data JPA with Hibernate
- Database: PostgreSQL only, always running in a Docker container (no embedded database, no local database allowed)
- Testing: @SpringBootTest with Testcontainers for integration tests only
- Runtime: ABSOLUTE AND NON-NEGOTIABLE REQUIREMENT — All code execution (development, testing, production, any environment) MUST run exclusively inside Docker containers. Never run the application, tests, or database directly on the local host machine. No local execution instructions allowed.

Mandatory deliverables:
- Updated Dockerfile if needed (multi-stage: build with Maven, run the JAR)
- Updated docker-compose.yml to orchestrate the application container and PostgreSQL container

Produce a clear project plan that includes:

- Complete updated list of files and packages (highlight new or modified files)
- Brief description of each new or modified component's responsibility
- Separate .puml files containing full PlantUML source code for the following updated diagrams:
  - data-model.puml: class diagram showing User, Role, and the many-to-many relationship
  - use-cases.puml: use case diagram with an actor interacting with all use cases (User CRUD, Role CRUD, and managing roles on a User)
  - sequence-create-role.puml
  - sequence-list-roles.puml
  - sequence-get-role.puml
  - sequence-update-role.puml
  - sequence-delete-role.puml
  - sequence-list-user-roles.puml
  - sequence-add-role-to-user.puml
  - sequence-remove-role-from-user.puml

Respect the constitution constraints strictly: keep everything extremely minimal, no extra layers, no additional features, no hot-reload, no devtools, no unnecessary configuration.
```
