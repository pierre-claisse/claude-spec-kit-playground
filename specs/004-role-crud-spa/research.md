# Research: Role CRUD Single-Page Application

**Feature**: 004-role-crud-spa | **Date**: 2026-02-06

## Navigation Approach

**Decision**: Material Button Toggle Group (MatButtonToggleModule)

**Rationale**:
- Already using Angular Material in the project
- Button toggle group provides clear visual indication of active state
- Simpler than tabs (no panel content switching logic)
- No Angular Router required - just a component property controlling view visibility
- Minimal additional CSS needed

**Alternatives Considered**:
| Alternative | Why Rejected |
|-------------|--------------|
| Angular Router | Adds complexity, routing module, URL management - overkill for 2 views |
| Material Tabs | Requires content projection setup, more boilerplate than toggle |
| Plain buttons + CSS | Less accessible, manual active state management |

## View Switching Implementation

**Decision**: Single `activeView` property with ngIf directives

**Rationale**:
- Simplest approach: `activeView: 'users' | 'roles' = 'users'`
- Two ngIf blocks in template: `@if (activeView === 'users')` and `@if (activeView === 'roles')`
- Data loads on view switch (lazy loading pattern)
- No state preservation needed between views (per spec - no such requirement)

**Implementation Pattern**:
```typescript
activeView: 'users' | 'roles' = 'users';

switchView(view: 'users' | 'roles'): void {
  this.activeView = view;
  if (view === 'users') {
    this.loadUsers();
  } else {
    this.loadRoles();
  }
}
```

## Role Form Design

**Decision**: Reuse same dialog pattern as User form with simpler single-field

**Rationale**:
- Consistent UX with existing User form dialog
- Only one field (Name) vs User's two fields (Name, Email)
- Same validation pattern: required field check on submit
- Same submit/cancel button layout

## Delete Error Handling for Assigned Roles

**Decision**: Display backend error message directly in snackbar

**Rationale**:
- Backend already returns descriptive error message when role is assigned to users
- No client-side check needed - rely on backend constraint
- Same error display pattern as other operations

**Expected Backend Response**:
```json
{
  "error": "Cannot delete role: role is assigned to users"
}
```

## Proxy Configuration

**Decision**: Add `/roles` route to both nginx.conf and proxy.conf.json

**Rationale**:
- nginx.conf for production (already proxies /users)
- proxy.conf.json for development (ng serve with --proxy-config)
- Same backend target: `http://app:8080`

## E2E Test Strategy

**Decision**: Separate test file for Role CRUD, shared setup with User tests

**Rationale**:
- Keeps test files focused and maintainable
- role-crud.cy.js covers: navigation, role list, create, edit, delete, delete failure
- Can run independently or together with user tests
- Same Cypress configuration and Docker setup

**Test for Delete Failure**:
1. Create a role via API
2. Assign role to a user via API (if possible) or ensure test data has assigned role
3. Attempt delete via UI
4. Verify error message displayed

## No Clarifications Needed

All technical decisions resolved. No [NEEDS CLARIFICATION] markers from spec remain.
