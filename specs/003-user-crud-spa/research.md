# Research: User CRUD Single-Page Application

**Feature**: 003-user-crud-spa
**Date**: 2026-02-05

## Overview

This document captures technical decisions, rationale, and alternatives considered for the User CRUD SPA frontend implementation.

## Decision 1: Frontend Framework

**Decision**: Angular 19.x (latest stable via Angular CLI)

**Rationale**:
- Explicitly specified by user in planning input
- Angular CLI provides standardized project structure and build tooling
- Built-in TypeScript support with strict mode
- Well-defined patterns for HTTP communication

**Alternatives Considered**:
| Alternative | Why Rejected |
|-------------|--------------|
| React | User explicitly chose Angular |
| Vue | User explicitly chose Angular |
| Vanilla JS | Cannot meet Angular Material requirement |

## Decision 2: UI Component Library

**Decision**: Angular Material (minimal set: MatTableModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatDialogModule)

**Rationale**:
- Explicitly specified by user in planning input
- Provides pre-built table, form, button, and dialog components
- Consistent Material Design styling without custom CSS
- Minimal import of only required modules

**Alternatives Considered**:
| Alternative | Why Rejected |
|-------------|--------------|
| PrimeNG | User explicitly chose Angular Material |
| Custom CSS | Would require more code for same functionality |
| Bootstrap | User explicitly chose Angular Material |

## Decision 3: TypeScript Configuration

**Decision**: Maximum strictness enabled (strict: true, noImplicitAny: true, strictNullChecks: true, useUnknownInCatchVariables: true)

**Rationale**:
- Explicitly specified by user in planning input
- Catches type errors at compile time
- No implicit 'any' prevents type safety bypasses
- Unknown in catch variables requires explicit type checking

**Configuration**:
```json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "useUnknownInCatchVariables": true
  }
}
```

## Decision 4: HTTP Client

**Decision**: Angular HttpClient with JSON

**Rationale**:
- Built into Angular, no additional dependencies
- Type-safe HTTP requests with generics
- Observable-based for async operations
- Automatic JSON parsing

**API Endpoints**:
| Operation | Method | Endpoint | Request Body | Response |
|-----------|--------|----------|--------------|----------|
| List users | GET | /users | - | User[] |
| Get user | GET | /users/{id} | - | User |
| Create user | POST | /users | { name, email } | User |
| Update user | PUT | /users/{id} | { name, email } | User |
| Delete user | DELETE | /users/{id} | - | 204 No Content |

## Decision 5: Architecture Pattern

**Decision**: Single component (AppComponent) handles all logic directly

**Rationale**:
- Constitution requires extreme minimalism and direct architecture
- No service layer needed for simple CRUD operations
- HttpClient calls directly in component
- Avoids unnecessary abstraction

**What We're NOT Doing** (per constitution):
- No UserService class
- No state management (NgRx, etc.)
- No interceptors
- No guards
- No separate dialog component (use MatDialog inline)

## Decision 6: Container Strategy

**Decision**: Multi-stage Docker builds for all environments

**Rationale**:
- User requirement: "All code execution MUST run exclusively inside Docker containers"
- Build stage: Node 20 + Angular CLI compiles the app
- Runtime stage: nginx:alpine serves static files
- Dev stage: Node 20 + ng serve with volume mount for hot reload

**Container Architecture**:
```
┌─────────────────────────────────────────────────────────────┐
│                    docker-compose.yml                        │
├─────────────────┬─────────────────┬─────────────────────────┤
│   PostgreSQL    │    Backend      │       Frontend          │
│   (db)          │    (app)        │       (frontend)        │
│   Port: 5432    │    Port: 8080   │       Port: 80/4200     │
│                 │                 │                         │
│   postgres:16   │  Spring Boot    │  nginx (prod)           │
│                 │  Java 21        │  ng serve (dev)         │
└─────────────────┴─────────────────┴─────────────────────────┘
```

## Decision 7: E2E Testing

**Decision**: Cypress with minimal configuration

**Rationale**:
- Explicitly specified by user in planning input
- Industry-standard for frontend e2e testing
- Can run in Docker (cypress/included image)
- Simple setup for basic CRUD flow verification

**Test Scope**:
1. List users (verify table displays data)
2. Create user (fill form, submit, verify in table)
3. Edit user (click edit, modify, submit, verify changes)
4. Delete user (click delete, confirm, verify removal)
5. Validation errors (empty fields)
6. Backend errors (display error message)

## Decision 8: API Proxy Strategy

**Decision**: nginx reverse proxy in frontend container

**Rationale**:
- Avoids CORS issues (same-origin requests)
- Frontend container proxies /users/* to backend container
- Clean separation: frontend serves static files + proxies API

**nginx.conf snippet**:
```nginx
location /users {
    proxy_pass http://app:8080;
}
```

## Decision 9: Form Implementation

**Decision**: Single Material dialog for both create and edit

**Rationale**:
- Reuses same form fields for create and edit
- Edit mode pre-fills fields with existing user data
- Create mode shows empty fields
- Minimizes code duplication

**Behavior**:
- "Create New User" button → opens dialog with empty form
- "Edit" button on row → opens same dialog with pre-filled data
- Submit → POST (create) or PUT (edit) based on mode
- Cancel → closes dialog without action

## Decision 10: Delete Confirmation

**Decision**: Native browser confirm() dialog

**Rationale**:
- Spec assumption states: "confirmation dialog will use browser's native confirm()"
- Simplest implementation
- No additional Material dialog needed

**Alternative Considered**: MatDialog for consistent styling - rejected as over-engineering per constitution.

## Resolved Clarifications

All technical choices were explicitly provided by the user. No NEEDS CLARIFICATION items remained after planning input.

## References

- Angular CLI: https://angular.dev/tools/cli
- Angular Material: https://material.angular.io/
- Cypress Docker: https://docs.cypress.io/guides/continuous-integration/introduction#Docker
- nginx Proxy: https://nginx.org/en/docs/http/ngx_http_proxy_module.html
