# Quickstart: User CRUD Single-Page Application

**Feature**: 003-user-crud-spa
**Date**: 2026-02-05

## Prerequisites

- Docker Desktop installed and running
- No local Node.js, Angular CLI, or Java required (all runs in containers)

## Production Mode

Start the full stack (PostgreSQL + Backend + Frontend with nginx):

```bash
docker-compose up -d
```

Access the application:
- **Frontend**: http://localhost:80
- **Backend API**: http://localhost:8080/users

Stop the stack:
```bash
docker-compose down
```

## Development Mode (Hot Reload)

Start the stack with live reloading for frontend development:

```bash
docker-compose -f docker-compose.dev.yml up -d
```

Access:
- **Frontend (ng serve)**: http://localhost:4200
- **Backend API**: http://localhost:8080/users

The frontend container mounts `./frontend/src` as a volume. Changes to source files trigger automatic recompilation and browser refresh.

Stop:
```bash
docker-compose -f docker-compose.dev.yml down
```

## Running E2E Tests

Run Cypress e2e tests against the full stack:

```bash
docker-compose -f docker-compose.e2e.yml up --abort-on-container-exit
```

This will:
1. Start PostgreSQL, Backend, and Frontend containers
2. Wait for all services to be healthy
3. Run Cypress tests
4. Exit with test results

View test results in the terminal output.

## Manual API Testing

### List Users

```bash
curl http://localhost:8080/users
```

Expected response (empty initially):
```json
[]
```

### Create User

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice", "email": "alice@example.com"}'
```

Expected response:
```json
{"id":1,"name":"Alice","email":"alice@example.com"}
```

### Get User by ID

```bash
curl http://localhost:8080/users/1
```

Expected response:
```json
{"id":1,"name":"Alice","email":"alice@example.com"}
```

### Update User

```bash
curl -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice Updated", "email": "alice.new@example.com"}'
```

Expected response:
```json
{"id":1,"name":"Alice Updated","email":"alice.new@example.com"}
```

### Delete User

```bash
curl -X DELETE http://localhost:8080/users/1 -w "\n%{http_code}\n"
```

Expected response:
```
204
```

### Error Cases

**Missing required field (400 Bad Request)**:
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name": ""}' \
  -w "\n%{http_code}\n"
```

Expected: `400`

**Non-existent user (404 Not Found)**:
```bash
curl http://localhost:8080/users/999 -w "\n%{http_code}\n"
```

Expected: `404`

## Container Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         Host Machine                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐   ┌─────────────┐   ┌─────────────────┐   │
│  │ PostgreSQL  │   │   Backend   │   │    Frontend     │   │
│  │ (db)        │   │   (app)     │   │   (frontend)    │   │
│  │ Port: 5432  │◄──│ Port: 8080  │◄──│ Port: 80/4200   │   │
│  │             │   │             │   │                 │   │
│  │ postgres:16 │   │ Java 21     │   │ nginx (prod)    │   │
│  │             │   │ Spring Boot │   │ ng serve (dev)  │   │
│  └─────────────┘   └─────────────┘   └─────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Troubleshooting

### Frontend not loading

Check if all containers are running:
```bash
docker-compose ps
```

Check frontend logs:
```bash
docker-compose logs frontend
```

### API requests failing

Verify backend is healthy:
```bash
curl http://localhost:8080/users
```

Check backend logs:
```bash
docker-compose logs app
```

### Database connection issues

Check PostgreSQL is ready:
```bash
docker-compose logs db
```

Verify database health:
```bash
docker-compose exec db pg_isready -U postgres
```

### Rebuild after code changes

```bash
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

## File Structure After Setup

```
.
├── docker-compose.yml          # Production: nginx serving built app
├── docker-compose.dev.yml      # Development: ng serve with hot reload
├── docker-compose.e2e.yml      # E2E tests: Cypress
├── Dockerfile                  # Backend (existing)
├── frontend/
│   ├── Dockerfile              # Frontend production build
│   ├── Dockerfile.dev          # Frontend development
│   ├── nginx.conf              # nginx configuration
│   └── src/                    # Angular source code
└── src/                        # Backend source code (existing)
```
