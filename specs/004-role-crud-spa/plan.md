# Implementation Plan: Role CRUD Single-Page Application

**Branch**: `004-role-crud-spa` | **Date**: 2026-02-06 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/004-role-crud-spa/spec.md`

## Summary

Extend the existing Angular single-page application (003-user-crud-spa) to add Role CRUD functionality alongside the existing User CRUD. Add a navigation mechanism (Material button group or tabs) to switch between User and Role views. Roles have simpler data (only Name field). The frontend will display roles in a table, allow creation/editing via a form dialog, and deletion with confirmation. Special handling for delete errors when a role is assigned to users. All execution (dev, test, build, prod) runs exclusively in Docker containers.

## Technical Context

**Language/Version**: TypeScript 5.x (Angular CLI default) with strict mode enabled (strict: true, noImplicitAny: true, strictNullChecks: true, useUnknownInCatchVariables: true)
**Primary Dependencies**: Angular 19.x (latest stable), Angular Material (minimal: table, form fields, buttons, dialog, button group/tabs), Angular HttpClient
**Storage**: N/A (frontend only - backend uses PostgreSQL in container)
**Testing**: Cypress (e2e tests in Docker)
**Target Platform**: Web browser (served via nginx in Docker container)
**Project Type**: Web application (extending existing frontend + unchanged backend)
**Performance Goals**: Page load < 2 seconds, view switch < 1 second, operations complete < 30 seconds (per spec SC-001 through SC-005)
**Constraints**: All execution in Docker containers only (dev, test, build, prod)
**Scale/Scope**: Single-page application, 1 component extended with Role CRUD, view switching, 8 total CRUD operations (4 User + 4 Role)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. YAGNI-First | ✅ PASS | Only implementing requested Role CRUD + navigation, no extras |
| II. Extreme Minimalism | ✅ PASS | Extending single AppComponent, no new services, direct HttpClient calls |
| III. Stripped-Down Features | ✅ PASS | No pagination, sorting, caching, i18n - only required field validation for Name |
| IV. Direct Architecture | ✅ PASS | No DTOs, no service layer, AppComponent handles all logic directly |
| V. Testing Discipline | ✅ PASS | E2E tests explicitly requested by user (navigation, Role CRUD, delete failure) |
| Technical Constraints | ⚠️ JUSTIFIED | User explicitly requires Docker-only execution (overrides in-memory DB for dev) |

**Justified Violation**: Constitution states "MUST use in-memory database for development" but user explicitly requires "All code execution MUST run exclusively inside Docker containers" - this is a user requirement that supersedes the default constitution constraint.

## Project Structure

### Documentation (this feature)

```text
specs/004-role-crud-spa/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (API documentation)
├── diagrams/            # PlantUML diagrams
│   ├── data-model.puml
│   ├── use-cases-frontend.puml
│   ├── sequence-list-users.puml     (copied from 003)
│   ├── sequence-create-user.puml    (copied from 003)
│   ├── sequence-update-user.puml    (copied from 003)
│   ├── sequence-delete-user.puml    (copied from 003)
│   ├── sequence-list-roles.puml
│   ├── sequence-create-role.puml
│   ├── sequence-update-role.puml
│   ├── sequence-delete-role.puml
│   └── sequence-switch-view.puml
├── checklists/          # Quality checklists
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```text
# Existing backend (unchanged)
src/
├── main/java/com/example/usercrud/
│   ├── User.java
│   ├── UserRepository.java
│   ├── UserController.java
│   ├── Role.java
│   ├── RoleRepository.java
│   └── RoleController.java
└── test/java/com/example/usercrud/
    ├── UserControllerIntegrationTest.java
    └── RoleControllerIntegrationTest.java

# Frontend (modified files marked with *)
frontend/
├── Dockerfile                    # Unchanged
├── Dockerfile.dev                # Unchanged
├── nginx.conf                    # * Updated: add /roles proxy
├── angular.json                  # Unchanged
├── package.json                  # Unchanged
├── tsconfig.json                 # Unchanged
├── src/
│   ├── index.html                # Unchanged
│   ├── main.ts                   # Unchanged
│   ├── styles.css                # Unchanged
│   └── app/
│       ├── app.component.ts      # * Extended: add Role state, methods, navigation
│       ├── app.component.html    # * Extended: add navigation, Role table, Role form
│       ├── app.component.css     # * Extended: add navigation styles
│       ├── app.config.ts         # * Updated: add MatButtonToggleModule
│       ├── user.model.ts         # Unchanged
│       └── role.model.ts         # * New: Role interface (id, name)
├── cypress.config.js             # Unchanged
└── cypress/
    ├── e2e/
    │   ├── user-crud.cy.js       # Unchanged
    │   └── role-crud.cy.js       # * New: Role CRUD + navigation tests
    └── support/
        └── e2e.js                # Unchanged

# Root level (unchanged or minimally updated)
docker-compose.yml                # * Updated: add /roles proxy route
docker-compose.dev.yml            # * Updated: add /roles proxy route
docker-compose.e2e.yml            # Unchanged
Dockerfile                        # Unchanged (backend)
```

**Structure Decision**: Extend existing frontend Angular project. No new components, services, or modules. All logic added to existing AppComponent. New role.model.ts for TypeScript interface.

## Component Responsibilities

| Component | Responsibility |
|-----------|----------------|
| `AppComponent` (extended) | Handles all UI: navigation between User/Role views, user table, user form, role table, role form, delete confirmations, API calls via HttpClient, success/error messages |
| `role.model.ts` (new) | TypeScript interface defining Role shape: `{ id: number, name: string }` |
| `app.config.ts` (updated) | Add MatButtonToggleModule import for navigation toggle buttons |
| `nginx.conf` (updated) | Add proxy for `/roles` API requests to backend container |
| `proxy.conf.json` (updated) | Add `/roles` proxy for development mode |
| `cypress/e2e/role-crud.cy.js` (new) | E2E tests for navigation between views, Role CRUD flow, delete failure, validation |

## Navigation Design

The navigation will use Material Button Toggle Group for view switching:
- Two toggle buttons: "Users" and "Roles"
- Default active: "Users"
- Visual indication of active view via Material toggle selection
- View switching controls visibility of User table/actions vs Role table/actions
- No Angular Router required - simple ngIf/ngSwitch on a component property

## API Endpoints (existing backend)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/roles` | GET | List all roles |
| `/roles` | POST | Create new role |
| `/roles/{id}` | GET | Get role by ID |
| `/roles/{id}` | PUT | Update role |
| `/roles/{id}` | DELETE | Delete role (fails if assigned to users) |

## E2E Test Coverage (new)

| Test Category | Tests |
|---------------|-------|
| Navigation | Switch from Users to Roles, switch from Roles to Users, verify active state |
| Role List | Display roles table, empty state |
| Role Create | Open form, submit valid, validation error on empty name |
| Role Edit | Open with pre-filled data, update successfully |
| Role Delete | Confirm and delete, cancel delete, delete failure when assigned |
| Error Handling | API errors displayed, backend error messages shown |

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Docker-only dev | User explicit requirement: "ABSOLUTE AND NON-NEGOTIABLE" | Constitution default (in-memory DB) contradicts user requirement |
