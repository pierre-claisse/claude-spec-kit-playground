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

After this, run tasks, analyse and implement commands.

```
/speckit.specify Create a single-page application frontend that interacts exclusively with the existing backend REST API to perform CRUD operations on User resources only.

The application must be contained in a single HTML page with inline JavaScript and CSS.

Required UI features:
- A table listing all users, with columns: ID, Name, Email, and action buttons (Edit, Delete) for each row
- A "Create New User" button that opens a form (modal or inline section) with fields: Name (required), Email (required)
- Edit button on each row opens the same form pre-filled with the user's data for update
- Delete button on each row shows a confirmation dialog before deleting
- After successful create, update, or delete, refresh the user list automatically and show a brief success message
- On API errors, display the error message returned by the backend

All interactions must be asynchronous (fetch API calls to the exact backend endpoints: POST /users, GET /users, GET /users/{id}, PUT /users/{id}, DELETE /users/{id}) and update the DOM dynamically without page reloads.

No routing, no multiple pages, no tabs, no additional features (no sorting, searching, pagination, validation beyond required fields, or any Role-related functionality).

Include basic end-to-end tests that start both the backend and frontend and verify the full CRUD flow with valid and invalid inputs.
```

```
/speckit.plan
Plan the implementation of the single-page application frontend that interacts with the existing backend REST API for User CRUD operations only.

Use the following technical choices:

- Framework: latest stable Angular (via Angular CLI)
- Language: TypeScript configured for maximum strictness (strict: true, noImplicitAny: true, strictNullChecks: true, useUnknownInCatchVariables: true, no fallback to 'any')
- UI components: Angular Material (minimal set of components needed: table, form fields, buttons, dialog for confirm/modals)
- No routing (single root component handles everything)
- HTTP client: Angular HttpClient with JSON
- Build tool: Angular CLI (standard configuration, no extras)

Backend remains unchanged (Spring Boot 3.5.10, Maven, PostgreSQL in container).

Runtime: ABSOLUTE AND NON-NEGOTIABLE REQUIREMENT — All code execution (development, testing, building, production, any environment) for both backend and frontend MUST run exclusively inside Docker containers. Never run the application, ng serve, tests, builds, or database directly on the local host machine. No local execution instructions allowed.

Mandatory deliverables:
- Separate frontend directory with full Angular project structure
- Dockerfile for frontend (multi-stage: build stage with node/Angular CLI, runtime stage with nginx serving dist folder)
- Optional dev Dockerfile or compose override for ng serve in a container (hot-reload capable)
- Updated docker-compose.yml that orchestrates:
  - PostgreSQL container
  - Backend application container
  - Frontend container (nginx serving the built app for prod, or ng serve container for dev)

End-to-end tests:
- Use Cypress (minimal configuration) for basic e2e tests
- Tests must start the full stack (backend + database + frontend) via docker-compose and verify the complete User CRUD flow (list, create, edit, delete) including success cases, validation (required fields), and error handling

Produce a clear project plan that includes:

- Complete updated project structure (highlight frontend files and any modifications)
- Brief description of each new or modified component's responsibility (focus on Angular components: e.g., AppComponent containing table + form/modal)
- Separate .puml files containing full PlantUML source code for:
  - data-model.puml: class diagram showing backend entities (User and Role with many-to-many)
  - use-cases-frontend.puml: use case diagram with an actor (User) interacting with frontend use cases (List Users, Create User, Update User, Delete User)
  - sequence-list-users.puml
  - sequence-create-user.puml
  - sequence-update-user.puml
  - sequence-delete-user.puml
  - (Each sequence diagram must show the browser/frontend calling the backend REST endpoint and updating the UI)

Respect the constitution constraints strictly: keep everything extremely minimal, no extra components/modules/services, no guards, no interceptors, no advanced Material features, no unnecessary configuration.
```

After this, run tasks, analyse and implement commands.

```
/speckit.specify Extend the existing single-page application frontend to also support full CRUD operations on Role resources, while keeping the existing User CRUD functionality.

Add a clean, intuitive navigation mechanism to switch between the User management view and the Role management view (e.g., tabs, buttons, or a simple menu that clearly shows which section is active).

For Roles:
- A table listing all roles, with columns: ID, Name, and action buttons (Edit, Delete) for each row
- A "Create New Role" button that opens a form with field: Name (required)
- Edit button on each row opens the same form pre-filled with the role's data for update
- Delete button on each row shows a confirmation dialog before deleting; if deletion fails because the role is assigned to users, display the error message from the backend
- After successful create, update, or delete, refresh the role list automatically and show a brief success message
- On API errors, display the error message returned by the backend

All Role interactions must be asynchronous (fetch API calls to the exact backend endpoints: POST /roles, GET /roles, GET /roles/{id}, PUT /roles/{id}, DELETE /roles/{id}) and update the DOM dynamically without page reloads.

The navigation must allow seamless switching between the two views while preserving the single-page nature (no full page reloads, no external routing).

No features for assigning or removing roles from users, no bulk operations, no sorting, searching, pagination, or any additional functionality.

Include basic end-to-end tests that start both the backend and frontend and verify:
- Navigation between User and Role views
- Full Role CRUD flow (list, create, edit, delete) with valid inputs
- Delete failure when role is assigned
- Error handling and required field enforcement
```

```
/speckit.plan Plan the extension of the existing Angular frontend to include full CRUD operations on Role resources and intuitive navigation between User and Role management views.

Use the exact same technical choices as before:
- Framework: latest stable Angular (via Angular CLI)
- Language: TypeScript configured for maximum strictness (strict: true, noImplicitAny: true, strictNullChecks: true, useUnknownInCatchVariables: true, no fallback to 'any')
- UI components: Angular Material (minimal set: table, form fields, buttons, dialog, tabs or button group for navigation)
- No routing module (use simple component visibility toggling or Material tabs for view switching)
- HTTP client: Angular HttpClient with JSON
- Build tool: Angular CLI (standard configuration, no extras)

Backend remains unchanged.

Runtime: ABSOLUTE AND NON-NEGOTIABLE REQUIREMENT — All code execution (development, testing, building, production, any environment) for both backend and frontend MUST run exclusively inside Docker containers. Never run the application, ng serve, tests, builds, or database directly on the local host machine. No local execution instructions allowed.

Mandatory deliverables:
- Updated frontend Angular project (no structural changes beyond new components/templates)
- Dockerfile for frontend unchanged or minimally updated if needed
- Updated docker-compose.yml that continues to orchestrate:
  - PostgreSQL container
  - Backend application container
  - Frontend container (nginx for prod, optional ng serve container for dev)

End-to-end tests:
- Extend existing Cypress tests to also cover:
  - Navigation between User and Role views
  - Full Role CRUD flow (list, create, edit, delete) with valid inputs
  - Delete failure when role is assigned (error message display)
  - Required field enforcement and API error handling

Produce a clear project plan that includes:

- Complete updated project structure (highlight new or modified frontend files)
- Brief description of each new or modified component's responsibility (e.g., new RoleListComponent, RoleFormComponent, navigation handling in AppComponent)
- Separate .puml files containing full PlantUML source code for the updated diagrams:
  - data-model.puml: class diagram showing backend entities (User and Role with many-to-many, unchanged)
  - use-cases-frontend.puml: updated use case diagram with an actor interacting with all frontend use cases (List/Create/Update/Delete Users + List/Create/Update/Delete Roles + Switch View)
  - sequence-list-users.puml (unchanged)
  - sequence-create-user.puml (unchanged)
  - sequence-update-user.puml (unchanged)
  - sequence-delete-user.puml (unchanged)
  - sequence-list-roles.puml
  - sequence-create-role.puml
  - sequence-update-role.puml
  - sequence-delete-role.puml
  - sequence-switch-view.puml (simple sequence showing user action toggling between User and Role views)
  - (Each sequence diagram must show the browser/frontend calling the backend REST endpoint and updating the UI)

Respect the constitution constraints strictly: keep everything extremely minimal, no extra components/modules/services, no advanced features, no unnecessary configuration.
```

After this, run tasks, analyse and implement commands.
