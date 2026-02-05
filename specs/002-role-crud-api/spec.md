# Feature Specification: Role CRUD API

**Feature Branch**: `002-role-crud-api`
**Created**: 2026-02-05
**Status**: Draft
**Input**: User description: "Extend the existing REST API to add Role resources with full CRUD operations and a many-to-many relationship with User."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Create Role (Priority: P1)

A client creates a new role by providing a unique name. The system stores the role and returns the created role data with a generated unique ID.

**Why this priority**: Creating roles is the foundation for all other role operations and user-role assignments. Without roles in the system, no other operations can proceed.

**Independent Test**: Can be fully tested by sending a POST request with a valid role name and verifying the response contains the role with a generated ID.

**Acceptance Scenarios**:

1. **Given** an empty role database, **When** client sends POST /roles with name "admin", **Then** system returns HTTP 201 with role data including a generated unique ID and name.
2. **Given** a role with name "admin" exists, **When** client sends POST /roles with name "admin", **Then** system returns HTTP 409 Conflict with error details indicating the name is already taken.
3. **Given** an empty role database, **When** client sends POST /roles with missing name, **Then** system returns HTTP 400 with error details.
4. **Given** an empty role database, **When** client sends POST /roles with empty name, **Then** system returns HTTP 400 with error details.

---

### User Story 2 - Read Roles (Priority: P2)

A client retrieves all roles or a specific role by ID. The system returns the requested role data in JSON format.

**Why this priority**: Reading data is essential for verification and building upon created roles. Enables clients to confirm operations succeeded and discover available roles.

**Independent Test**: After creating roles via Story 1, can test by sending GET requests and verifying correct role data is returned.

**Acceptance Scenarios**:

1. **Given** roles exist in the database, **When** client sends GET /roles, **Then** system returns HTTP 200 with a JSON array of all roles.
2. **Given** a role with ID 1 exists, **When** client sends GET /roles/1, **Then** system returns HTTP 200 with that role's data.
3. **Given** no role with ID 999 exists, **When** client sends GET /roles/999, **Then** system returns HTTP 404 with error details.
4. **Given** an empty database, **When** client sends GET /roles, **Then** system returns HTTP 200 with an empty JSON array.

---

### User Story 3 - Update Role (Priority: P3)

A client updates an existing role's name. The system persists the changes and returns the updated role data.

**Why this priority**: Modifying existing roles allows correcting mistakes and adapting to changing requirements.

**Independent Test**: After creating a role via Story 1, can test by sending PUT request with new name and verifying the changes are returned and persisted.

**Acceptance Scenarios**:

1. **Given** a role with ID 1 exists with name "admin", **When** client sends PUT /roles/1 with name "administrator", **Then** system returns HTTP 200 with updated role data showing name "administrator".
2. **Given** no role with ID 999 exists, **When** client sends PUT /roles/999 with any data, **Then** system returns HTTP 404 with error details.
3. **Given** roles "admin" (ID 1) and "user" (ID 2) exist, **When** client sends PUT /roles/1 with name "user", **Then** system returns HTTP 409 Conflict with error details indicating the name is already taken.
4. **Given** a role with ID 1 exists, **When** client sends PUT /roles/1 with missing or empty name, **Then** system returns HTTP 400 with error details.

---

### User Story 4 - Delete Role (Priority: P4)

A client deletes a role by ID. The system removes the role only if it is not assigned to any user; otherwise returns an error.

**Why this priority**: Deletion is typically less frequent and includes a safety constraint to prevent orphaned references.

**Independent Test**: After creating a role via Story 1, can test by sending DELETE request and verifying the role no longer exists (when unassigned) or receives conflict error (when assigned).

**Acceptance Scenarios**:

1. **Given** a role with ID 1 exists and is not assigned to any user, **When** client sends DELETE /roles/1, **Then** system returns HTTP 204 (no content) and the role is removed.
2. **Given** no role with ID 999 exists, **When** client sends DELETE /roles/999, **Then** system returns HTTP 404 with error details.
3. **Given** a role with ID 1 is assigned to at least one user, **When** client sends DELETE /roles/1, **Then** system returns HTTP 409 Conflict with error message indicating the role is in use.

---

### User Story 5 - Assign Role to User (Priority: P5)

A client assigns an existing role to an existing user. The system creates the association and confirms the assignment.

**Why this priority**: This connects the User and Role entities, enabling the many-to-many relationship that gives roles their practical value.

**Independent Test**: After creating a user and a role, can test by sending POST request to assign the role and verifying the assignment exists.

**Acceptance Scenarios**:

1. **Given** user ID 1 and role ID 1 exist and role is not assigned to user, **When** client sends POST /users/1/roles/1, **Then** system returns HTTP 200 with the role data that was assigned.
2. **Given** no user with ID 999 exists, **When** client sends POST /users/999/roles/1, **Then** system returns HTTP 404 with error details indicating user not found.
3. **Given** no role with ID 999 exists, **When** client sends POST /users/1/roles/999, **Then** system returns HTTP 404 with error details indicating role not found.
4. **Given** role ID 1 is already assigned to user ID 1, **When** client sends POST /users/1/roles/1, **Then** system returns HTTP 409 Conflict with error details indicating the role is already assigned.

---

### User Story 6 - View User's Roles (Priority: P6)

A client retrieves all roles assigned to a specific user.

**Why this priority**: Viewing assignments is needed to verify role assignments and display user permissions.

**Independent Test**: After assigning roles to a user, can test by sending GET request and verifying the correct roles are returned.

**Acceptance Scenarios**:

1. **Given** user ID 1 has roles "admin" and "user" assigned, **When** client sends GET /users/1/roles, **Then** system returns HTTP 200 with JSON array containing both roles.
2. **Given** user ID 1 exists but has no roles assigned, **When** client sends GET /users/1/roles, **Then** system returns HTTP 200 with empty JSON array.
3. **Given** no user with ID 999 exists, **When** client sends GET /users/999/roles, **Then** system returns HTTP 404 with error details indicating user not found.

---

### User Story 7 - Remove Role from User (Priority: P7)

A client removes a role assignment from a user.

**Why this priority**: Revoking role assignments completes the role management lifecycle.

**Independent Test**: After assigning a role to a user, can test by sending DELETE request and verifying the assignment no longer exists.

**Acceptance Scenarios**:

1. **Given** role ID 1 is assigned to user ID 1, **When** client sends DELETE /users/1/roles/1, **Then** system returns HTTP 204 (no content) and the role is no longer assigned to the user.
2. **Given** no user with ID 999 exists, **When** client sends DELETE /users/999/roles/1, **Then** system returns HTTP 404 with error details indicating user not found.
3. **Given** no role with ID 999 exists, **When** client sends DELETE /users/1/roles/999, **Then** system returns HTTP 404 with error details indicating role not found.
4. **Given** role ID 1 is not assigned to user ID 1, **When** client sends DELETE /users/1/roles/1, **Then** system returns HTTP 404 with error details indicating the assignment does not exist.

---

### Edge Cases

- What happens when client sends invalid JSON in request body? System returns HTTP 400 with error details.
- What happens when client sends non-integer ID in URL path? System returns HTTP 400 with error details.
- What happens when client sends empty request body to POST /roles? System returns HTTP 400 with error details.
- What happens when client sends empty request body to PUT /roles/{id}? System returns HTTP 400 with error details.
- What happens when trying to assign a role that was just deleted? System returns HTTP 404 indicating role not found.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST expose POST /roles endpoint that accepts JSON with name, creates a role with unique name constraint, and returns the created role with a generated unique ID.
- **FR-002**: System MUST expose GET /roles endpoint that returns a JSON array of all roles.
- **FR-003**: System MUST expose GET /roles/{id} endpoint that returns a single role by ID or HTTP 404 if not found.
- **FR-004**: System MUST expose PUT /roles/{id} endpoint that updates role name and returns the updated role, or HTTP 404 if not found.
- **FR-005**: System MUST expose DELETE /roles/{id} endpoint that removes a role and returns HTTP 204 if not assigned to any user, HTTP 409 if assigned to users, or HTTP 404 if not found.
- **FR-006**: System MUST expose GET /users/{userId}/roles endpoint that returns a JSON array of roles assigned to the specified user, or HTTP 404 if user not found.
- **FR-007**: System MUST expose POST /users/{userId}/roles/{roleId} endpoint that assigns a role to a user, returning the assigned role, HTTP 404 if user or role not found, or HTTP 409 if already assigned.
- **FR-008**: System MUST expose DELETE /users/{userId}/roles/{roleId} endpoint that removes a role assignment from a user, returning HTTP 204 on success, or HTTP 404 if user, role, or assignment not found.
- **FR-009**: System MUST auto-generate a unique ID for each role upon creation.
- **FR-010**: System MUST enforce uniqueness constraint on role names (case-sensitive).
- **FR-011**: System MUST require name field (non-empty string) when creating or updating a role.
- **FR-012**: System MUST return appropriate HTTP status codes: 200 (OK), 201 (Created), 204 (No Content), 400 (Bad Request), 404 (Not Found), 409 (Conflict).
- **FR-013**: System MUST return JSON responses for all endpoints.
- **FR-014**: System MUST persist role data and user-role assignments to the database.
- **FR-015**: System MUST support many-to-many relationship between users and roles (a user can have multiple roles, a role can be assigned to multiple users).

### Key Entities

- **Role**: Represents a permission group in the system. Attributes: unique ID (auto-generated), name (required string, unique).
- **UserRole (Join)**: Represents the many-to-many relationship between User and Role. Links a user ID to a role ID.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All eight new endpoints are accessible and return correct responses for valid requests.
- **SC-002**: Invalid requests (missing required fields, non-existent IDs, duplicate names) return appropriate error responses.
- **SC-003**: Role data persists across requests (create a role, retrieve it, data matches).
- **SC-004**: User-role assignments persist correctly (assign a role, retrieve user's roles, assignment is present).
- **SC-005**: Role uniqueness is enforced (attempting to create or update to a duplicate name returns 409).
- **SC-006**: Role deletion is blocked when role is assigned to users (returns 409 with clear error message).
- **SC-007**: Integration tests verify all endpoints work end-to-end with a real database.

## Assumptions

- Role name uniqueness is case-sensitive ("Admin" and "admin" are different roles).
- Role IDs are integers (auto-incremented or similar).
- No authentication or authorization is required.
- No pagination, filtering, or sorting is required for list endpoints.
- PUT endpoint requires the name field to be provided (full replacement, not partial update).
- The existing User entity and endpoints remain unchanged except for the new relationship.
- When a user is deleted, their role assignments are automatically removed (cascade delete on the join relationship).
- The response for POST /users/{userId}/roles/{roleId} returns the assigned role data (not the user or a simple confirmation).
