# Tasks: Role CRUD API

**Input**: Design documents from `/specs/002-role-crud-api/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Integration tests are requested per SC-007 in spec.md: "Integration tests verify all endpoints work end-to-end with a real database."

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Single project**: `src/main/java/com/example/usercrud/` at repository root
- **Tests**: `src/test/java/com/example/usercrud/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

This feature extends an existing project. No setup tasks required.

**Checkpoint**: Existing project structure validated - proceed to Foundational phase

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core entities and relationships that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T001 [P] Create Role entity with id and unique name in src/main/java/com/example/usercrud/Role.java
- [ ] T002 [P] Create RoleRepository interface extending JpaRepository in src/main/java/com/example/usercrud/RoleRepository.java
- [ ] T003 Modify User entity to add @ManyToMany relationship to Role in src/main/java/com/example/usercrud/User.java

**Checkpoint**: Foundation ready - Role entity exists, User has roles relationship, user story implementation can now begin

---

## Phase 3: User Story 1 - Create Role (Priority: P1) 🎯 MVP

**Goal**: A client creates a new role by providing a unique name. System returns 201 with role data or 409 on duplicate name.

**Independent Test**: POST /roles with valid name returns 201; POST with duplicate name returns 409; POST with missing/empty name returns 400.

### Implementation for User Story 1

- [ ] T004 [US1] Create RoleController class with POST /roles endpoint in src/main/java/com/example/usercrud/RoleController.java
- [ ] T005 [US1] Add existsByName query method to RoleRepository for uniqueness check in src/main/java/com/example/usercrud/RoleRepository.java
- [ ] T006 [US1] Implement 400 validation for missing/empty name in POST /roles in src/main/java/com/example/usercrud/RoleController.java
- [ ] T007 [US1] Implement 409 Conflict response for duplicate role name in POST /roles in src/main/java/com/example/usercrud/RoleController.java

**Checkpoint**: Create role endpoint functional - can create roles with unique names

---

## Phase 4: User Story 2 - Read Roles (Priority: P2)

**Goal**: A client retrieves all roles or a specific role by ID. System returns 200 with role data or 404 if not found.

**Independent Test**: GET /roles returns array of roles; GET /roles/{id} returns role or 404.

### Implementation for User Story 2

- [ ] T008 [US2] Implement GET /roles endpoint returning all roles in src/main/java/com/example/usercrud/RoleController.java
- [ ] T009 [US2] Implement GET /roles/{id} endpoint returning role or 404 in src/main/java/com/example/usercrud/RoleController.java

**Checkpoint**: Read role endpoints functional - can list all roles and get by ID

---

## Phase 5: User Story 3 - Update Role (Priority: P3)

**Goal**: A client updates an existing role's name. System returns 200 with updated data, 404 if not found, or 409 on duplicate name.

**Independent Test**: PUT /roles/{id} with new name returns 200; PUT with non-existent ID returns 404; PUT with duplicate name returns 409.

### Implementation for User Story 3

- [ ] T010 [US3] Implement PUT /roles/{id} endpoint with name update in src/main/java/com/example/usercrud/RoleController.java
- [ ] T011 [US3] Add 400 validation for missing/empty name in PUT /roles/{id} in src/main/java/com/example/usercrud/RoleController.java
- [ ] T012 [US3] Add 409 Conflict check for duplicate name on update in PUT /roles/{id} in src/main/java/com/example/usercrud/RoleController.java

**Checkpoint**: Update role endpoint functional - can modify role names with uniqueness enforcement

---

## Phase 6: User Story 4 - Delete Role (Priority: P4)

**Goal**: A client deletes a role by ID. System returns 204 if successful, 404 if not found, or 409 if role is assigned to users.

**Independent Test**: DELETE /roles/{id} for unassigned role returns 204; DELETE for non-existent ID returns 404; DELETE for assigned role returns 409.

### Implementation for User Story 4

- [ ] T013 [US4] Add countUsersByRoleId custom query to RoleRepository in src/main/java/com/example/usercrud/RoleRepository.java
- [ ] T014 [US4] Implement DELETE /roles/{id} endpoint with 204/404 responses in src/main/java/com/example/usercrud/RoleController.java
- [ ] T015 [US4] Add 409 Conflict check for role assigned to users in DELETE /roles/{id} in src/main/java/com/example/usercrud/RoleController.java

**Checkpoint**: Delete role endpoint functional - can delete unassigned roles, blocks deletion of assigned roles

---

## Phase 7: User Story 5 - Assign Role to User (Priority: P5)

**Goal**: A client assigns an existing role to an existing user. System returns 200 with role data, 404 if user/role not found, or 409 if already assigned.

**Independent Test**: POST /users/{userId}/roles/{roleId} for valid unassigned pair returns 200; POST for non-existent user/role returns 404; POST for already assigned returns 409.

### Implementation for User Story 5

- [ ] T016 [US5] Implement POST /users/{userId}/roles/{roleId} endpoint in src/main/java/com/example/usercrud/UserController.java
- [ ] T017 [US5] Add RoleRepository dependency injection to UserController in src/main/java/com/example/usercrud/UserController.java
- [ ] T018 [US5] Add 404 response for non-existent user or role in POST /users/{userId}/roles/{roleId} in src/main/java/com/example/usercrud/UserController.java
- [ ] T019 [US5] Add 409 Conflict response for already assigned role in POST /users/{userId}/roles/{roleId} in src/main/java/com/example/usercrud/UserController.java

**Checkpoint**: Assign role endpoint functional - can assign roles to users with duplicate prevention

---

## Phase 8: User Story 6 - View User's Roles (Priority: P6)

**Goal**: A client retrieves all roles assigned to a specific user. System returns 200 with role array or 404 if user not found.

**Independent Test**: GET /users/{userId}/roles returns array of assigned roles; GET for non-existent user returns 404.

### Implementation for User Story 6

- [ ] T020 [US6] Implement GET /users/{userId}/roles endpoint in src/main/java/com/example/usercrud/UserController.java
- [ ] T021 [US6] Add 404 response for non-existent user in GET /users/{userId}/roles in src/main/java/com/example/usercrud/UserController.java

**Checkpoint**: View user roles endpoint functional - can list roles assigned to a user

---

## Phase 9: User Story 7 - Remove Role from User (Priority: P7)

**Goal**: A client removes a role assignment from a user. System returns 204 on success or 404 if user/role/assignment not found.

**Independent Test**: DELETE /users/{userId}/roles/{roleId} for existing assignment returns 204; DELETE for non-existent user/role/assignment returns 404.

### Implementation for User Story 7

- [ ] T022 [US7] Implement DELETE /users/{userId}/roles/{roleId} endpoint in src/main/java/com/example/usercrud/UserController.java
- [ ] T023 [US7] Add 404 response for non-existent user, role, or assignment in DELETE /users/{userId}/roles/{roleId} in src/main/java/com/example/usercrud/UserController.java

**Checkpoint**: Remove role endpoint functional - can remove role assignments from users

---

## Phase 10: Testing & Polish

**Purpose**: Integration tests and validation across all user stories

### Integration Tests

- [ ] T024 Create RoleControllerIntegrationTest class with Testcontainers setup in src/test/java/com/example/usercrud/RoleControllerIntegrationTest.java
- [ ] T025 [P] Add integration tests for US1: Create Role (201, 400, 409 scenarios) in src/test/java/com/example/usercrud/RoleControllerIntegrationTest.java
- [ ] T026 [P] Add integration tests for US2: Read Roles (200, 404 scenarios) in src/test/java/com/example/usercrud/RoleControllerIntegrationTest.java
- [ ] T027 [P] Add integration tests for US3: Update Role (200, 400, 404, 409 scenarios) in src/test/java/com/example/usercrud/RoleControllerIntegrationTest.java
- [ ] T028 [P] Add integration tests for US4: Delete Role (204, 404, 409 scenarios) in src/test/java/com/example/usercrud/RoleControllerIntegrationTest.java
- [ ] T029 [P] Add integration tests for US5: Assign Role (200, 404, 409 scenarios) in src/test/java/com/example/usercrud/RoleControllerIntegrationTest.java
- [ ] T030 [P] Add integration tests for US6: View User Roles (200, 404 scenarios) in src/test/java/com/example/usercrud/RoleControllerIntegrationTest.java
- [ ] T031 [P] Add integration tests for US7: Remove Role (204, 404 scenarios) in src/test/java/com/example/usercrud/RoleControllerIntegrationTest.java

### Validation

- [ ] T032 Run all integration tests via docker-compose.test.yml and verify pass
- [ ] T033 Run quickstart.md API examples and verify expected responses

**Checkpoint**: All tests passing, quickstart validated

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: N/A - extending existing project
- **Foundational (Phase 2)**: No dependencies - creates core entities
- **User Stories (Phase 3-9)**: All depend on Foundational phase completion
  - US1-US4 (Role CRUD): Depend only on Foundational
  - US5-US7 (User-Role): Depend on Foundational (User.roles field)
- **Testing & Polish (Phase 10)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational - creates role functionality
- **User Story 2 (P2)**: Can start after US1 tasks create RoleController
- **User Story 3 (P3)**: Can start after US1 tasks create RoleController
- **User Story 4 (P4)**: Can start after US1 tasks create RoleController
- **User Story 5 (P5)**: Can start after Foundational - independent of Role CRUD
- **User Story 6 (P6)**: Can start after Foundational - independent of Role CRUD
- **User Story 7 (P7)**: Can start after Foundational - independent of Role CRUD

### Within Each User Story

- Foundational entities before controllers
- Core endpoint before validation/error handling
- Story complete before moving to next priority

### Parallel Opportunities

- T001 and T002 can run in parallel (different files)
- T008 and T009 can run in parallel (same file but independent methods)
- T025-T031 can run in parallel (test methods in same file)
- US5, US6, US7 can be worked on in parallel (all modify UserController but independent methods)

---

## Parallel Example: Foundational Phase

```bash
# Launch foundational entity creation together:
Task: "Create Role entity in src/main/java/com/example/usercrud/Role.java"
Task: "Create RoleRepository in src/main/java/com/example/usercrud/RoleRepository.java"
# Then after both complete:
Task: "Modify User entity to add @ManyToMany relationship"
```

## Parallel Example: Integration Tests

```bash
# After T024 creates test class, launch all test methods together:
Task: "Add integration tests for US1: Create Role"
Task: "Add integration tests for US2: Read Roles"
Task: "Add integration tests for US3: Update Role"
Task: "Add integration tests for US4: Delete Role"
Task: "Add integration tests for US5: Assign Role"
Task: "Add integration tests for US6: View User Roles"
Task: "Add integration tests for US7: Remove Role"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 2: Foundational (Role entity, repository, User relationship)
2. Complete Phase 3: User Story 1 (Create Role)
3. **STOP and VALIDATE**: Test POST /roles independently
4. Deploy/demo if ready - basic role creation works

### Incremental Delivery

1. Complete Foundational → Foundation ready
2. Add User Story 1 → Create roles → Minimal value
3. Add User Story 2 → Read roles → Can verify created roles
4. Add User Story 3 → Update roles → Full CRUD minus delete
5. Add User Story 4 → Delete roles → Complete Role CRUD
6. Add User Story 5 → Assign roles → Connect users and roles
7. Add User Story 6 → View assignments → Verify assignments
8. Add User Story 7 → Remove assignments → Complete feature
9. Add Tests → Validate all functionality

### Suggested Development Order

For a single developer:
1. T001, T002 (parallel) → T003 (sequential)
2. T004-T007 (US1 sequential)
3. T008-T009 (US2)
4. T010-T012 (US3)
5. T013-T015 (US4)
6. T016-T019 (US5)
7. T020-T021 (US6)
8. T022-T023 (US7)
9. T024-T033 (Tests & validation)

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- All tests run inside Docker via Testcontainers (no local database)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- All execution must be in Docker containers per project constraints
