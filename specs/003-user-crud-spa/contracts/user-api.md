# User API Contract

**Base URL**: `http://localhost:8080` (backend container)
**Frontend Proxy**: `http://localhost:80/users` → `http://app:8080/users`

## Endpoints

### GET /users

List all users.

**Request**:
```http
GET /users HTTP/1.1
Accept: application/json
```

**Response 200 OK**:
```json
[
  {
    "id": 1,
    "name": "Alice",
    "email": "alice@example.com"
  },
  {
    "id": 2,
    "name": "Bob",
    "email": "bob@example.com"
  }
]
```

**Response (empty database)**:
```json
[]
```

---

### GET /users/{id}

Get a single user by ID.

**Request**:
```http
GET /users/1 HTTP/1.1
Accept: application/json
```

**Response 200 OK**:
```json
{
  "id": 1,
  "name": "Alice",
  "email": "alice@example.com"
}
```

**Response 404 Not Found**:
```http
HTTP/1.1 404 Not Found
```

---

### POST /users

Create a new user.

**Request**:
```http
POST /users HTTP/1.1
Content-Type: application/json

{
  "name": "Alice",
  "email": "alice@example.com"
}
```

**Response 201 Created**:
```json
{
  "id": 1,
  "name": "Alice",
  "email": "alice@example.com"
}
```

**Response 400 Bad Request** (validation error):
```json
{
  "timestamp": "2026-02-05T12:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/users"
}
```

**Validation Rules**:
- `name`: Must not be blank
- `email`: Must not be blank

---

### PUT /users/{id}

Update an existing user.

**Request**:
```http
PUT /users/1 HTTP/1.1
Content-Type: application/json

{
  "name": "Alice Updated",
  "email": "alice.new@example.com"
}
```

**Response 200 OK**:
```json
{
  "id": 1,
  "name": "Alice Updated",
  "email": "alice.new@example.com"
}
```

**Response 404 Not Found** (user does not exist):
```http
HTTP/1.1 404 Not Found
```

**Notes**:
- Partial updates supported: only provided fields are updated
- If `name` is null in request, existing name is preserved
- If `email` is null in request, existing email is preserved

---

### DELETE /users/{id}

Delete a user.

**Request**:
```http
DELETE /users/1 HTTP/1.1
```

**Response 204 No Content**:
```http
HTTP/1.1 204 No Content
```

**Response 404 Not Found** (user does not exist):
```http
HTTP/1.1 404 Not Found
```

---

## Error Responses

### 400 Bad Request

Returned when request validation fails.

```json
{
  "timestamp": "2026-02-05T12:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/users"
}
```

**Common causes**:
- Missing required field (name or email)
- Blank field (empty string or whitespace only)

### 404 Not Found

Returned when the requested resource does not exist.

```http
HTTP/1.1 404 Not Found
```

**Common causes**:
- GET/PUT/DELETE with non-existent user ID

### 500 Internal Server Error

Returned on unexpected server errors.

```json
{
  "timestamp": "2026-02-05T12:00:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/users"
}
```

---

## Data Types

### User

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | integer | Read-only | Auto-generated unique identifier |
| name | string | Yes | User's display name (not blank) |
| email | string | Yes | User's email address (not blank) |

### Request Body (Create/Update)

| Field | Type | Required for Create | Required for Update |
|-------|------|---------------------|---------------------|
| name | string | Yes | No (preserves existing if null) |
| email | string | Yes | No (preserves existing if null) |

---

## Frontend Integration Notes

### HttpClient Usage (Angular)

```typescript
// List all users
this.http.get<User[]>('/users')

// Get single user
this.http.get<User>(`/users/${id}`)

// Create user
this.http.post<User>('/users', { name, email })

// Update user
this.http.put<User>(`/users/${id}`, { name, email })

// Delete user
this.http.delete<void>(`/users/${id}`)
```

### Error Handling

```typescript
this.http.post<User>('/users', user).subscribe({
  next: (created) => { /* success */ },
  error: (err: HttpErrorResponse) => {
    // Display err.error.error or err.message to user
  }
});
```

### CORS

The frontend container (nginx) proxies API requests to the backend container. This avoids CORS issues since all requests originate from the same host from the browser's perspective.

```
Browser → nginx:80/users → app:8080/users
```
