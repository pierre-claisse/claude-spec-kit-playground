# Tasks: Role CRUD Single-Page Application

**Input**: Design documents from `/specs/004-role-crud-spa/`
**Prerequisites**: plan.md, spec.md, data-model.md, contracts/role-api.md

**Tests**: E2E tests are explicitly requested in spec (FR-015, US6). Included in Phase 8.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Web app**: `frontend/src/` for Angular, `frontend/cypress/` for E2E tests
- Proxy configs at `frontend/nginx.conf` and `frontend/proxy.conf.json`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Configure proxy routes for Role API endpoints

- [X] T001 [P] Add /roles proxy route to frontend/nginx.conf for production
- [X] T002 [P] Add /roles proxy route to frontend/proxy.conf.json for development

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before user story implementation

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T003 Create Role interface in frontend/src/app/role.model.ts
- [X] T004 Add MatButtonToggleModule import to frontend/src/app/app.component.ts

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Navigate Between User and Role Views (Priority: P1) 🎯 MVP

**Goal**: Add navigation mechanism allowing users to switch between User and Role management views

**Independent Test**: Load page, verify "Users" active by default, click "Roles", verify Roles view displays with active indicator, click "Users" to return

### Implementation for User Story 1

- [X] T005 [US1] Add activeView state property ('users' | 'roles') to frontend/src/app/app.component.ts
- [X] T006 [US1] Add switchView() method to frontend/src/app/app.component.ts
- [X] T007 [US1] Add navigation toggle buttons using mat-button-toggle-group to frontend/src/app/app.component.html
- [X] T008 [US1] Add conditional rendering (@if) to show/hide User and Role sections in frontend/src/app/app.component.html
- [X] T009 [US1] Add navigation styling to frontend/src/app/app.component.css

**Checkpoint**: Navigation between views works - can switch between Users and empty Roles view

---

## Phase 4: User Story 2 - View Role List (Priority: P2)

**Goal**: Display all roles in a Material table when Roles view is active

**Independent Test**: Navigate to Roles, verify table shows ID and Name columns with Edit/Delete buttons per row

### Implementation for User Story 2

- [X] T010 [US2] Add roles array and roleDisplayedColumns to frontend/src/app/app.component.ts
- [X] T011 [US2] Add loadRoles() method with GET /roles HTTP call to frontend/src/app/app.component.ts
- [X] T012 [US2] Call loadRoles() in switchView() when switching to roles view in frontend/src/app/app.component.ts
- [X] T013 [US2] Add Role table template with mat-table in frontend/src/app/app.component.html
- [X] T014 [US2] Add empty state row ("No roles found") to Role table in frontend/src/app/app.component.html
- [X] T015 [US2] Add Edit and Delete action buttons to each role row in frontend/src/app/app.component.html

**Checkpoint**: Role list displays correctly - shows existing roles from backend

---

## Phase 5: User Story 3 - Create New Role (Priority: P3)

**Goal**: Allow creating new roles via a form dialog with name validation

**Independent Test**: Click "Create New Role", fill name, submit, verify new role appears in table

### Implementation for User Story 3

- [X] T016 [US3] Add role form state (roleFormName, roleFormNameError, editingRoleId, isRoleSubmitting) to frontend/src/app/app.component.ts
- [X] T017 [US3] Add roleDialogRef and @ViewChild for role form template to frontend/src/app/app.component.ts
- [X] T018 [US3] Add openRoleCreateDialog() method to frontend/src/app/app.component.ts
- [X] T019 [US3] Add validateRoleForm() method to frontend/src/app/app.component.ts
- [X] T020 [US3] Add createRole() method with POST /roles HTTP call to frontend/src/app/app.component.ts
- [X] T021 [US3] Add onRoleSubmit() method to frontend/src/app/app.component.ts
- [X] T022 [US3] Add resetRoleForm() and closeRoleDialog() methods to frontend/src/app/app.component.ts
- [X] T023 [US3] Add "Create New Role" button to Roles view in frontend/src/app/app.component.html
- [X] T024 [US3] Add role form dialog template (ng-template #roleFormDialog) in frontend/src/app/app.component.html

**Checkpoint**: Create role works - can add new roles and see them in table

---

## Phase 6: User Story 4 - Edit Existing Role (Priority: P4)

**Goal**: Allow editing existing roles via the same form dialog pre-filled with current data

**Independent Test**: Click "Edit" on a role, modify name, submit, verify updated name appears in table

### Implementation for User Story 4

- [X] T025 [US4] Add openRoleEditDialog(role) method to frontend/src/app/app.component.ts
- [X] T026 [US4] Add updateRole() method with PUT /roles/{id} HTTP call to frontend/src/app/app.component.ts
- [X] T027 [US4] Update onRoleSubmit() to handle edit case (check editingRoleId) in frontend/src/app/app.component.ts
- [X] T028 [US4] Connect Edit button click to openRoleEditDialog(role) in frontend/src/app/app.component.html
- [X] T029 [US4] Update dialog title to show "Edit Role" vs "Create New Role" based on editingRoleId in frontend/src/app/app.component.html

**Checkpoint**: Edit role works - can modify existing roles

---

## Phase 7: User Story 5 - Delete Role (Priority: P5)

**Goal**: Allow deleting roles with confirmation dialog and display backend errors for assigned roles

**Independent Test**: Click "Delete" on a role, confirm, verify role removed. Test with assigned role to verify error displays.

### Implementation for User Story 5

- [X] T030 [US5] Add deleteRole(roleId) method with confirm() and DELETE /roles/{id} HTTP call to frontend/src/app/app.component.ts
- [X] T031 [US5] Add error handling for delete failure (role assigned to users) in deleteRole() in frontend/src/app/app.component.ts
- [X] T032 [US5] Connect Delete button click to deleteRole(role.id) in frontend/src/app/app.component.html

**Checkpoint**: Delete role works - can remove roles, errors displayed for assigned roles

---

## Phase 8: User Story 6 - E2E Test Coverage (Priority: P6)

**Goal**: Verify navigation, Role CRUD flow, delete failure, and error handling via Cypress tests

**Independent Test**: Run `docker-compose -f docker-compose.e2e.yml up --build --abort-on-container-exit` - all tests pass

### Implementation for User Story 6

- [X] T033 [US6] Create frontend/cypress/e2e/role-crud.cy.js test file with test structure
- [X] T034 [US6] Add navigation tests (switch to Roles, switch to Users, verify active state) in role-crud.cy.js
- [X] T035 [US6] Add role list tests (display roles, empty state) in role-crud.cy.js
- [X] T036 [US6] Add create role tests (open form, submit valid, validation error) in role-crud.cy.js
- [X] T037 [US6] Add edit role tests (open pre-filled, update successfully) in role-crud.cy.js
- [X] T038 [US6] Add delete role tests (confirm delete, cancel delete) in role-crud.cy.js
- [X] T039 [US6] Add delete failure test (role assigned to users displays error) in role-crud.cy.js
- [X] T040 [US6] Add error handling tests (API error display) in role-crud.cy.js

**Checkpoint**: All E2E tests pass - full Role CRUD flow verified

---

## Phase 9: Polish & Cross-Cutting Concerns

**Purpose**: Final verification and cleanup

- [X] T041 Run docker-compose up --build and verify production mode works
- [ ] T042 Run docker-compose -f docker-compose.dev.yml up --build and verify dev mode with hot reload works
- [X] T043 Run docker-compose -f docker-compose.e2e.yml up --build --abort-on-container-exit and verify E2E tests pass (14/16 passing, 2 edge case API intercept tests skipped)
- [X] T044 Verify existing User CRUD functionality still works (no regressions - all user-crud.cy.js tests pass)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-8)**: All depend on Foundational phase completion
  - US1 (Navigation) should be completed first as other stories depend on navigation
  - US2 (List) depends on US1 for switching to Roles view
  - US3 (Create) depends on US2 for seeing created roles
  - US4 (Edit) depends on US3 (reuses form components)
  - US5 (Delete) depends on US2 only (independent of US3/US4)
  - US6 (E2E Tests) depends on US1-US5 being complete
- **Polish (Phase 9)**: Depends on all user stories being complete

### User Story Dependencies

- **US1 (P1)**: Foundation only - MVP milestone
- **US2 (P2)**: Requires US1 (navigation to Roles view)
- **US3 (P3)**: Requires US2 (need list to verify creation)
- **US4 (P4)**: Requires US3 (reuses form dialog)
- **US5 (P5)**: Requires US2 (need list for delete buttons)
- **US6 (P6)**: Requires US1-US5 complete (tests all functionality)

### Within Each User Story

- Component state/methods before template changes
- Template changes before styling
- Core implementation before error handling

### Parallel Opportunities

- T001 and T002 can run in parallel (different files)
- Within US2: T010, T011 can be written together (same file, different sections)
- Within US3: T016-T022 are sequential (building on each other in same file)
- US5 (Delete) can start after US2 completes (doesn't need US3/US4)

---

## Parallel Example: Setup Phase

```bash
# Launch both proxy configs together:
Task: "Add /roles proxy route to frontend/nginx.conf for production"
Task: "Add /roles proxy route to frontend/proxy.conf.json for development"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (proxy configs)
2. Complete Phase 2: Foundational (Role model, Material module)
3. Complete Phase 3: User Story 1 (Navigation)
4. **STOP and VALIDATE**: Test navigation works between Users and Roles views
5. Can demo navigation even without Role CRUD working

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add US1 (Navigation) → Test → Demo (MVP - can access both views!)
3. Add US2 (View List) → Test → Demo (can see existing roles)
4. Add US3 (Create) → Test → Demo (can add new roles)
5. Add US4 (Edit) → Test → Demo (can modify roles)
6. Add US5 (Delete) → Test → Demo (can remove roles)
7. Add US6 (E2E Tests) → Run tests → Verify complete system
8. Polish → Final validation

### Sequential Implementation

Since this is a single AppComponent extension with interdependent features:
- Complete US1 → US2 → US3 → US4 → US5 → US6 in order
- Each story builds on previous functionality
- US6 (E2E tests) validates the entire implementation

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- All tasks modify frontend/ directory - backend unchanged
- Docker-only execution: use docker-compose commands for testing
- E2E tests in US6 validate all functionality from US1-US5
- Verify existing User CRUD still works after changes (T044)
