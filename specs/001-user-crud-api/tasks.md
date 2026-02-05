# Tasks: User CRUD API

**Input**: Design documents from `/specs/001-user-crud-api/`
**Prerequisites**: plan.md, spec.md, data-model.md, contracts/openapi.yaml, research.md, quickstart.md

**Tests**: Integration tests are explicitly requested in the feature specification.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

- **Single project**: Standard Maven layout at repository root
- `src/main/java/com/example/usercrud/` for Java sources
- `src/main/resources/` for configuration
- `src/test/java/com/example/usercrud/` for tests

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and Docker configuration

- [x] T001 Create Maven project directory structure: src/main/java/com/example/usercrud/, src/main/resources/, src/test/java/com/example/usercrud/
- [x] T002 Create pom.xml with Spring Boot 3.5.10 parent, spring-boot-starter-web, spring-boot-starter-data-jpa, postgresql, spring-boot-starter-test, testcontainers dependencies
- [x] T003 [P] Create Dockerfile with multi-stage build (Maven build stage, JRE runtime stage)
- [x] T004 [P] Create docker-compose.yml with app and PostgreSQL containers

**Checkpoint**: Project structure ready, Docker files created

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**CRITICAL**: No user story work can begin until this phase is complete

- [x] T005 Create application.properties in src/main/resources/ with PostgreSQL connection using environment variables
- [x] T006 Create UserCrudApplication.java in src/main/java/com/example/usercrud/ with @SpringBootApplication
- [x] T007 Create User.java JPA entity in src/main/java/com/example/usercrud/ with id (Long, @Id, @GeneratedValue), name (@NotBlank), email (@NotBlank)
- [x] T008 Create UserRepository.java interface in src/main/java/com/example/usercrud/ extending JpaRepository<User, Long>
- [x] T009 Create UserController.java in src/main/java/com/example/usercrud/ with @RestController, @RequestMapping("/users"), and UserRepository injection

**Checkpoint**: Foundation ready - application can start and connect to database

---

## Phase 3: User Story 1 - Create User (Priority: P1) MVP

**Goal**: Client can create a new user by providing name and email

**Independent Test**: POST /users with valid data returns 201 with user including generated ID

### Implementation for User Story 1

- [x] T010 [US1] Implement POST /users endpoint in src/main/java/com/example/usercrud/UserController.java: accept JSON body with name/email, save to repository, return 201 with saved user

### Tests for User Story 1

- [x] T011 [US1] Create UserControllerIntegrationTest.java in src/test/java/com/example/usercrud/ with @SpringBootTest, Testcontainers PostgreSQL setup
- [x] T012 [US1] Add test: POST /users with valid name and email returns 201 and user with generated ID
- [x] T013 [US1] Add test: POST /users with missing name returns 400
- [x] T014 [US1] Add test: POST /users with missing email returns 400

**Checkpoint**: User Story 1 complete - can create users via POST /users

---

## Phase 4: User Story 2 - Read Users (Priority: P2)

**Goal**: Client can retrieve all users or a specific user by ID

**Independent Test**: GET /users returns list; GET /users/{id} returns user or 404

### Implementation for User Story 2

- [x] T015 [US2] Implement GET /users endpoint in src/main/java/com/example/usercrud/UserController.java: return list of all users
- [x] T016 [US2] Implement GET /users/{id} endpoint in src/main/java/com/example/usercrud/UserController.java: return user by ID or 404 if not found

### Tests for User Story 2

- [x] T017 [US2] Add test: GET /users returns 200 with JSON array of users
- [x] T018 [US2] Add test: GET /users on empty database returns 200 with empty array
- [x] T019 [US2] Add test: GET /users/{id} with existing ID returns 200 with user data
- [x] T020 [US2] Add test: GET /users/{id} with non-existent ID returns 404

**Checkpoint**: User Stories 1 and 2 complete - can create and read users

---

## Phase 5: User Story 3 - Update User (Priority: P3)

**Goal**: Client can update an existing user's name and/or email

**Independent Test**: PUT /users/{id} with valid data returns 200 with updated user or 404

### Implementation for User Story 3

- [x] T021 [US3] Implement PUT /users/{id} endpoint in src/main/java/com/example/usercrud/UserController.java: find user, update provided fields, save, return updated user or 404

### Tests for User Story 3

- [x] T022 [US3] Add test: PUT /users/{id} with new name updates and returns 200
- [x] T023 [US3] Add test: PUT /users/{id} with new email updates and returns 200
- [x] T024 [US3] Add test: PUT /users/{id} with non-existent ID returns 404

**Checkpoint**: User Stories 1-3 complete - can create, read, and update users

---

## Phase 6: User Story 4 - Delete User (Priority: P4)

**Goal**: Client can delete a user by ID

**Independent Test**: DELETE /users/{id} returns 204 and user is removed, or 404 if not found

### Implementation for User Story 4

- [x] T025 [US4] Implement DELETE /users/{id} endpoint in src/main/java/com/example/usercrud/UserController.java: check if exists, delete, return 204 or 404

### Tests for User Story 4

- [x] T026 [US4] Add test: DELETE /users/{id} with existing ID returns 204 and user is removed
- [x] T027 [US4] Add test: DELETE /users/{id} with non-existent ID returns 404

**Checkpoint**: All user stories complete - full CRUD functionality

---

## Phase 7: Polish & Validation

**Purpose**: Final validation and documentation

- [x] T028 Build and run application with docker-compose up --build
- [x] T029 Validate all quickstart.md instructions work correctly
- [x] T030 Run all integration tests via docker-compose -f docker-compose.test.yml run --rm test

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational phase completion
  - User stories can proceed in priority order (P1 → P2 → P3 → P4)
  - Or in parallel if team capacity allows (after Foundational)
- **Polish (Phase 7)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational - Independent of US1
- **User Story 3 (P3)**: Can start after Foundational - Independent of US1/US2
- **User Story 4 (P4)**: Can start after Foundational - Independent of US1/US2/US3

### Within Each User Story

- Implementation before tests (tests require endpoint to exist for integration testing)
- All tests in same file (UserControllerIntegrationTest.java)

### Parallel Opportunities

- T003 and T004 can run in parallel (different files)
- After Foundational, all user story implementations can theoretically run in parallel (different methods in same files)
- Tests for each user story can be added after their endpoint implementation

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: docker-compose up --build, test POST /users manually
5. Deploy/demo if ready

### Incremental Delivery

1. Setup + Foundational → Application starts
2. Add User Story 1 → Can create users (MVP!)
3. Add User Story 2 → Can read users
4. Add User Story 3 → Can update users
5. Add User Story 4 → Can delete users
6. Each story adds value without breaking previous stories

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story is independently testable once Foundational is complete
- All tests go in single file: UserControllerIntegrationTest.java
- Controller methods added incrementally to UserController.java
- Commit after each phase or logical group
- All execution must be in Docker containers (no local mvn or java commands)
