# Implementation Plan: User CRUD Single-Page Application

**Branch**: `003-user-crud-spa` | **Date**: 2026-02-05 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/003-user-crud-spa/spec.md`

## Summary

Build a minimal Angular single-page application that provides a user interface for CRUD operations on User resources via the existing Spring Boot backend REST API. The frontend will display users in a table, allow creation/editing via a form dialog, and deletion with confirmation. All execution (dev, test, build, prod) runs exclusively in Docker containers.

## Technical Context

**Language/Version**: TypeScript 5.x (Angular CLI default) with strict mode enabled
**Primary Dependencies**: Angular 19.x (latest stable), Angular Material (minimal: table, form fields, buttons, dialog), Angular HttpClient
**Storage**: N/A (frontend only - backend uses PostgreSQL in container)
**Testing**: Cypress (e2e tests in Docker)
**Target Platform**: Web browser (served via nginx in Docker container)
**Project Type**: Web application (frontend + existing backend)
**Performance Goals**: Page load < 2 seconds, operations complete < 30 seconds (per spec SC-001 through SC-004)
**Constraints**: All execution in Docker containers only (dev, test, build, prod)
**Scale/Scope**: Single-page application, 1 component, 4 CRUD operations

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. YAGNI-First | ✅ PASS | Only implementing requested CRUD UI, no extras |
| II. Extreme Minimalism | ✅ PASS | Single component, no services layer, direct HttpClient calls |
| III. Stripped-Down Features | ✅ PASS | No pagination, sorting, caching, i18n - only required fields validation |
| IV. Direct Architecture | ✅ PASS | No DTOs, no service layer, AppComponent handles everything directly |
| V. Testing Discipline | ✅ PASS | E2E tests explicitly requested by user |
| Technical Constraints | ⚠️ JUSTIFIED | User explicitly requires Docker-only execution (overrides in-memory DB for dev) |

**Justified Violation**: Constitution states "MUST use in-memory database for development" but user explicitly requires "All code execution MUST run exclusively inside Docker containers" - this is a user requirement that supersedes the default constitution constraint.

## Project Structure

### Documentation (this feature)

```text
specs/003-user-crud-spa/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (API documentation)
├── diagrams/            # PlantUML diagrams
│   ├── data-model.puml
│   ├── use-cases-frontend.puml
│   ├── sequence-list-users.puml
│   ├── sequence-create-user.puml
│   ├── sequence-update-user.puml
│   └── sequence-delete-user.puml
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

# New frontend
frontend/
├── Dockerfile                    # Multi-stage: build (node/ng) + runtime (nginx)
├── Dockerfile.dev                # Dev container with ng serve for hot reload
├── nginx.conf                    # Nginx config for serving Angular app + API proxy
├── angular.json                  # Angular CLI configuration
├── package.json                  # Dependencies
├── tsconfig.json                 # TypeScript strict configuration
├── src/
│   ├── index.html
│   ├── main.ts
│   ├── styles.css
│   └── app/
│       ├── app.component.ts      # Main component: table + form dialog + all logic
│       ├── app.component.html    # Template with Material table, form, dialogs
│       ├── app.component.css     # Minimal styling
│       ├── app.config.ts         # App configuration (HttpClient, Material modules)
│       └── user.model.ts         # User interface (id, name, email)
└── cypress/
    ├── e2e/
    │   └── user-crud.cy.ts       # E2E tests for full CRUD flow
    ├── support/
    └── cypress.config.ts

# Root level (modified)
docker-compose.yml                # Updated: adds frontend service
docker-compose.dev.yml            # New: dev configuration with hot reload
docker-compose.e2e.yml            # New: e2e test configuration
Dockerfile                        # Existing backend Dockerfile (unchanged)
```

**Structure Decision**: Web application structure with separate frontend directory. Backend remains at repository root (existing Spring Boot project). Frontend is a standalone Angular project with its own Dockerfile for containerized execution.

## Component Responsibilities

| Component | Responsibility |
|-----------|----------------|
| `AppComponent` | Single component handling all UI: user table display, create/edit form dialog, delete confirmation, API calls via HttpClient, success/error message display |
| `user.model.ts` | TypeScript interface defining User shape: `{ id: number, name: string, email: string }` |
| `app.config.ts` | Standalone app configuration: HttpClient provider, Material modules import |
| `nginx.conf` | Serves static Angular build, proxies `/users` API requests to backend container |
| `Dockerfile` | Multi-stage build: Node 20 + Angular CLI for build, nginx:alpine for runtime |
| `Dockerfile.dev` | Node 20 container running `ng serve` with volume mount for hot reload |
| `cypress/e2e/user-crud.cy.ts` | E2E tests verifying list, create, edit, delete flows with valid/invalid inputs |

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Docker-only dev | User explicit requirement: "ABSOLUTE AND NON-NEGOTIABLE" | Constitution default (in-memory DB) contradicts user requirement |
| Separate frontend directory | Standard practice for Angular CLI projects | Single HTML file (spec FR-011) conflicts with Angular Material requirement |

**Note on FR-011**: The spec states "single HTML file with inline JavaScript and CSS" but user's planning input specifies Angular + Angular Material. Angular projects cannot realistically be a single HTML file. The planning input supersedes the spec's single-file constraint since Angular was explicitly chosen.
