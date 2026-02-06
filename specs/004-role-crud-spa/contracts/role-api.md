# Role API Contract

**Feature**: 004-role-crud-spa | **Date**: 2026-02-06

## Base URL

- Production: `http://localhost:80/roles` (via nginx proxy)
- Development: `http://localhost:4200/roles` (via Angular proxy)
- Backend direct: `http://localhost:8080/roles`

## Endpoints

### List All Roles

**Request**
```
GET /roles
```

**Response** `200 OK`
```json
[
  { "id": 1, "name": "Admin" },
  { "id": 2, "name": "User" }
]
```

**Response** (empty) `200 OK`
```json
[]
```

---

### Get Role by ID

**Request**
```
GET /roles/{id}
```

**Response** `200 OK`
```json
{ "id": 1, "name": "Admin" }
```

**Response** `404 Not Found`
```json
{
  "error": "Role not found"
}
```

---

### Create Role

**Request**
```
POST /roles
Content-Type: application/json

{ "name": "Manager" }
```

**Response** `201 Created`
```json
{ "id": 3, "name": "Manager" }
```

**Response** `400 Bad Request` (validation error)
```json
{
  "error": "Name is required"
}
```

---

### Update Role

**Request**
```
PUT /roles/{id}
Content-Type: application/json

{ "name": "Super Admin" }
```

**Response** `200 OK`
```json
{ "id": 1, "name": "Super Admin" }
```

**Response** `404 Not Found`
```json
{
  "error": "Role not found"
}
```

---

### Delete Role

**Request**
```
DELETE /roles/{id}
```

**Response** `204 No Content`
(empty body)

**Response** `404 Not Found`
```json
{
  "error": "Role not found"
}
```

**Response** `400 Bad Request` or `409 Conflict` (role assigned to users)
```json
{
  "error": "Cannot delete role: role is assigned to users"
}
```

## Error Response Format

All error responses follow this format:
```json
{
  "error": "Human-readable error message"
}
```

The frontend extracts the `error` field to display in the snackbar.

## Frontend Integration

### TypeScript Interface

```typescript
export interface Role {
  id: number;
  name: string;
}
```

### HttpClient Usage

```typescript
// List
this.http.get<Role[]>('/roles')

// Create
this.http.post<Role>('/roles', { name: 'New Role' })

// Update
this.http.put<Role>(`/roles/${id}`, { name: 'Updated Name' })

// Delete
this.http.delete<void>(`/roles/${id}`)
```

### Error Handling

```typescript
error: (err: HttpErrorResponse) => {
  const message = err.error?.error || err.message || 'Operation failed';
  this.showError(message);
}
```
