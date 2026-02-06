# Data Model: Role CRUD Single-Page Application

**Feature**: 004-role-crud-spa | **Date**: 2026-02-06

## Frontend Entities

### Role (TypeScript Interface)

```typescript
// frontend/src/app/role.model.ts
export interface Role {
  id: number;
  name: string;
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | number | Yes (server-generated) | Unique identifier |
| name | string | Yes | Role name (non-empty) |

### User (Existing - Unchanged)

```typescript
// frontend/src/app/user.model.ts
export interface User {
  id: number;
  name: string;
  email: string;
}
```

## Frontend State

### AppComponent State (Extended)

| Property | Type | Purpose |
|----------|------|---------|
| `activeView` | `'users' \| 'roles'` | Controls which view is displayed |
| `roles` | `Role[]` | Role list data for table |
| `roleDisplayedColumns` | `string[]` | Table columns: ['id', 'name', 'actions'] |
| `roleFormName` | `string` | Form input binding for role name |
| `roleFormNameError` | `string` | Validation error message |
| `editingRoleId` | `number \| null` | ID of role being edited, null for create |
| `roleDialogRef` | `MatDialogRef<unknown> \| null` | Reference to open dialog |

## Backend Entities (Existing - Unchanged)

The backend data model remains unchanged from feature 002-role-crud-api:

### User Entity

| Field | Type | Constraints |
|-------|------|-------------|
| id | Long | Primary key, auto-generated |
| name | String | Not null |
| email | String | Not null, unique |
| roles | Set<Role> | Many-to-many relationship |

### Role Entity

| Field | Type | Constraints |
|-------|------|-------------|
| id | Long | Primary key, auto-generated |
| name | String | Not null |
| users | Set<User> | Many-to-many (inverse side) |

## Validation Rules

### Role Form Validation (Frontend)

| Field | Rule | Error Message |
|-------|------|---------------|
| name | Must not be empty/whitespace | "Name is required" |

### Backend Validation (Existing)

| Endpoint | Validation | Error Response |
|----------|------------|----------------|
| DELETE /roles/{id} | Role not assigned to any users | 400/409 with error message |

## State Transitions

### View State

```
[Load App] → activeView = 'users' → loadUsers()
         ↓
[Click Roles] → activeView = 'roles' → loadRoles()
         ↓
[Click Users] → activeView = 'users' → loadUsers()
```

### Role Form State

```
[Create New Role] → roleFormName = '' → editingRoleId = null → openDialog
                                                              ↓
[Edit Role]       → roleFormName = role.name → editingRoleId = role.id → openDialog
                                                                         ↓
[Submit Valid]    → POST or PUT /roles → closeDialog → loadRoles → showSuccess
                                                                   ↓
[Submit Invalid]  → roleFormNameError = 'Name is required' → stay in dialog
                                                             ↓
[Cancel]          → closeDialog → reset form
```
