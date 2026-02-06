# Tasks: User CRUD Single-Page Application

**Input**: Design documents from `/specs/003-user-crud-spa/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/user-api.md

**Tests**: E2E tests are REQUIRED (spec SC-006, SC-007 explicitly require Cypress tests)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

- **Frontend**: `frontend/src/` (Angular project)
- **Cypress**: `frontend/cypress/` (E2E tests)
- **Docker**: Root level (`docker-compose.yml`, etc.)

---

## Phase 1: Setup (Angular Project Initialization)

**Purpose**: Create Angular project with required dependencies and TypeScript configuration

- [ ] T001 Create frontend directory and initialize Angular 19.x project with Angular CLI in frontend/
- [ ] T002 Configure TypeScript strict mode in frontend/tsconfig.json (strict: true, noImplicitAny: true, strictNullChecks: true, useUnknownInCatchVariables: true)
- [ ] T003 [P] Install Angular Material and configure minimal modules (MatTableModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatDialogModule, MatSnackBarModule) in frontend/src/app/app.config.ts
- [ ] T004 [P] Create User interface in frontend/src/app/user.model.ts with id, name, email fields

---

## Phase 2: Foundational (Docker Infrastructure)

**Purpose**: Docker containers and nginx configuration that MUST be complete before ANY user story implementation

**CRITICAL**: No user story work can begin until this phase is complete

- [ ] T005 Create production Dockerfile (multi-stage: Node 20 + Angular CLI build, nginx:alpine runtime) in frontend/Dockerfile
- [ ] T006 [P] Create development Dockerfile (Node 20 with ng serve) in frontend/Dockerfile.dev
- [ ] T007 [P] Create nginx configuration with API proxy (/users -> app:8080) in frontend/nginx.conf
- [ ] T008 Update docker-compose.yml to add frontend service (build from frontend/, ports 80:80, depends on app)
- [ ] T009 [P] Create docker-compose.dev.yml with frontend hot reload (volume mount frontend/src, port 4200)
- [ ] T010 [P] Create docker-compose.e2e.yml for Cypress tests (frontend + cypress/included container)
- [ ] T011 Initialize Cypress in frontend/cypress/ with cypress.config.ts and support files

**Checkpoint**: Docker infrastructure ready - user story implementation can now begin

---

## Phase 3: User Story 1 - View User List (Priority: P1) MVP

**Goal**: Display all users in a Material table with ID, Name, Email columns and action buttons

**Independent Test**: Load page, verify table displays data from GET /users endpoint

### Implementation for User Story 1

- [ ] T012 [US1] Create base AppComponent structure in frontend/src/app/app.component.ts with HttpClient injection and users array
- [ ] T013 [US1] Implement loadUsers() method calling GET /users in frontend/src/app/app.component.ts
- [ ] T014 [US1] Create Material table template in frontend/src/app/app.component.html with columns: ID, Name, Email, Actions
- [ ] T015 [P] [US1] Add basic styling for table in frontend/src/app/app.component.css
- [ ] T016 [US1] Implement error handling for loadUsers() - display error message on failure in frontend/src/app/app.component.ts
- [ ] T017 [US1] Call loadUsers() in ngOnInit() to load users on page load in frontend/src/app/app.component.ts

**Checkpoint**: User Story 1 complete - table displays users from backend. Test with: `docker-compose up -d`, open http://localhost:80

---

## Phase 4: User Story 2 - Create New User (Priority: P2)

**Goal**: "Create New User" button opens form dialog, submits to POST /users, refreshes table

**Independent Test**: Click "Create New User", fill form, submit, verify new user appears in table

### Implementation for User Story 2

- [ ] T018 [US2] Add "Create New User" button to template in frontend/src/app/app.component.html
- [ ] T019 [US2] Create form dialog template with name/email fields using ng-template in frontend/src/app/app.component.html
- [ ] T020 [US2] Implement openCreateDialog() method using MatDialog in frontend/src/app/app.component.ts
- [ ] T021 [US2] Implement form validation (required fields, error messages) in frontend/src/app/app.component.ts
- [ ] T022 [US2] Implement createUser() method calling POST /users in frontend/src/app/app.component.ts
- [ ] T023 [US2] Add success snackbar message after create using MatSnackBar in frontend/src/app/app.component.ts
- [ ] T024 [US2] Handle backend validation errors (400 Bad Request) and display to user in frontend/src/app/app.component.ts

**Checkpoint**: User Story 2 complete - can create new users via dialog

---

## Phase 5: User Story 3 - Edit Existing User (Priority: P3)

**Goal**: "Edit" button on row opens pre-filled form dialog, submits to PUT /users/{id}, refreshes table

**Independent Test**: Click "Edit" on row, modify fields, submit, verify updated data in table

### Implementation for User Story 3

- [ ] T025 [US3] Add "Edit" button to each row Actions column in frontend/src/app/app.component.html
- [ ] T026 [US3] Implement openEditDialog(user) method to open dialog with pre-filled data in frontend/src/app/app.component.ts
- [ ] T027 [US3] Implement updateUser() method calling PUT /users/{id} in frontend/src/app/app.component.ts
- [ ] T028 [US3] Add success snackbar message after update in frontend/src/app/app.component.ts
- [ ] T029 [US3] Handle 404 Not Found (user deleted by another) and display error in frontend/src/app/app.component.ts

**Checkpoint**: User Story 3 complete - can edit existing users via dialog

---

## Phase 6: User Story 4 - Delete User (Priority: P4)

**Goal**: "Delete" button on row shows browser confirm(), calls DELETE /users/{id}, refreshes table

**Independent Test**: Click "Delete" on row, confirm, verify user removed from table

### Implementation for User Story 4

- [ ] T030 [US4] Add "Delete" button to each row Actions column in frontend/src/app/app.component.html
- [ ] T031 [US4] Implement deleteUser(userId) method with native confirm() dialog in frontend/src/app/app.component.ts
- [ ] T032 [US4] Implement DELETE /users/{id} API call in frontend/src/app/app.component.ts
- [ ] T033 [US4] Add success snackbar message after delete in frontend/src/app/app.component.ts
- [ ] T034 [US4] Handle 404 Not Found (already deleted) and display error in frontend/src/app/app.component.ts

**Checkpoint**: User Story 4 complete - can delete users with confirmation

---

## Phase 7: E2E Tests & Polish

**Purpose**: Cypress E2E tests (required by spec SC-006, SC-007) and final validation

### E2E Tests (Required per spec)

- [ ] T035 [P] Write E2E test for US1: List users displays table with data in frontend/cypress/e2e/user-crud.cy.ts
- [ ] T036 [P] Write E2E test for US2: Create user via dialog in frontend/cypress/e2e/user-crud.cy.ts
- [ ] T037 [P] Write E2E test for US3: Edit user via dialog in frontend/cypress/e2e/user-crud.cy.ts
- [ ] T038 [P] Write E2E test for US4: Delete user with confirmation in frontend/cypress/e2e/user-crud.cy.ts
- [ ] T039 [P] Write E2E test for validation errors (empty fields) in frontend/cypress/e2e/user-crud.cy.ts
- [ ] T040 Run full E2E test suite via docker-compose.e2e.yml

### Final Validation

- [ ] T041 Validate quickstart.md commands work (production, dev, e2e modes)
- [ ] T042 Verify all success criteria (SC-001 through SC-007) are met

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup (T001-T004) - BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational phase (T005-T011) completion
  - US1 (P1): Can start after Foundational - MVP, no other story dependencies
  - US2 (P2): Depends on US1 (reuses table, loadUsers())
  - US3 (P3): Depends on US2 (reuses form dialog pattern)
  - US4 (P4): Depends on US1 (uses loadUsers(), table)
- **E2E Tests (Phase 7)**: Depends on all user stories (US1-US4) being complete

### User Story Dependencies

```
Phase 1 (Setup) ──► Phase 2 (Foundational) ──► US1 (List) ──► US2 (Create) ──► US3 (Edit)
                                                │                              │
                                                └──────────► US4 (Delete) ◄────┘
                                                                   │
                                                              Phase 7 (E2E)
```

### Within Each User Story

- Template changes before component logic (sometimes parallel)
- API integration before success/error handling
- Core functionality before edge cases

### Parallel Opportunities

**Phase 1:**
```
T001 (init Angular) ──► T002 (tsconfig)
T003 (Material) [P]
T004 (user.model.ts) [P]
```

**Phase 2:**
```
T005 (Dockerfile) ──► T008 (docker-compose.yml)
T006 (Dockerfile.dev) [P]
T007 (nginx.conf) [P]
T009 (docker-compose.dev.yml) [P]
T010 (docker-compose.e2e.yml) [P]
T011 (Cypress init)
```

**Phase 7 (E2E Tests):**
```
T035 (US1 test) [P]
T036 (US2 test) [P]
T037 (US3 test) [P]
T038 (US4 test) [P]
T039 (validation test) [P]
──► T040 (run all)
```

---

## Parallel Example: Phase 2 Foundational

```bash
# After T005 completes, launch these in parallel:
Task: "Create development Dockerfile in frontend/Dockerfile.dev"
Task: "Create nginx configuration in frontend/nginx.conf"
Task: "Create docker-compose.dev.yml"
Task: "Create docker-compose.e2e.yml"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T004)
2. Complete Phase 2: Foundational (T005-T011)
3. Complete Phase 3: User Story 1 (T012-T017)
4. **STOP and VALIDATE**: `docker-compose up -d`, open http://localhost:80
5. Table should display users from backend

### Incremental Delivery

1. Setup + Foundational → Docker infrastructure ready
2. Add US1 (List) → Test: table displays users → MVP!
3. Add US2 (Create) → Test: can add users via dialog
4. Add US3 (Edit) → Test: can edit users via dialog
5. Add US4 (Delete) → Test: can delete users with confirm
6. Add E2E Tests → Verify all stories via Cypress
7. Each story adds value without breaking previous stories

### Single Developer Strategy (Recommended)

Execute in strict priority order: P1 → P2 → P3 → P4 → E2E

```
Day 1: Setup + Foundational (T001-T011)
Day 2: US1 List Users (T012-T017) → Validate MVP
Day 3: US2 Create User (T018-T024)
Day 4: US3 Edit User (T025-T029)
Day 5: US4 Delete User (T030-T034)
Day 6: E2E Tests + Validation (T035-T042)
```

---

## Notes

- [P] tasks = different files, no dependencies on incomplete tasks
- [Story] label maps task to specific user story for traceability
- All code execution in Docker containers only (user requirement)
- Single AppComponent architecture per constitution (no service layer)
- Native browser confirm() for delete per spec assumption
- E2E tests required per spec SC-006, SC-007
- Commit after each task or logical group
- Stop at any checkpoint to validate independently
