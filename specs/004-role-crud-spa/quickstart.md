# Quickstart: Role CRUD Single-Page Application

**Feature**: 004-role-crud-spa | **Date**: 2026-02-06

## Prerequisites

- Docker Desktop installed and running
- Git (to clone repository)

## Docker Commands

All commands run from repository root.

### Production Mode

Build and run the complete application stack:

```bash
docker-compose up --build
```

Access the application at: http://localhost:80

Services started:
- PostgreSQL database (port 5432, internal only)
- Spring Boot backend (port 8080, internal only)
- Angular frontend via nginx (port 80)

### Development Mode (Hot Reload)

Run frontend with hot reload for development:

```bash
docker-compose -f docker-compose.dev.yml up --build
```

Access the application at: http://localhost:4200

Changes to frontend files trigger automatic rebuild.

### E2E Tests

Run Cypress end-to-end tests:

```bash
docker-compose -f docker-compose.e2e.yml up --build --abort-on-container-exit
```

Tests run automatically and container exits with test result code.

### Stop All Services

```bash
docker-compose down
docker-compose -f docker-compose.dev.yml down
docker-compose -f docker-compose.e2e.yml down
```

Remove volumes (database data):

```bash
docker-compose down -v
```

## Application Usage

### Navigation

1. Open application at http://localhost:80 (prod) or http://localhost:4200 (dev)
2. Default view: Users management
3. Click "Roles" toggle button to switch to Roles management
4. Click "Users" toggle button to switch back

### Role Operations

**List Roles**: Displayed automatically when Roles view is active

**Create Role**:
1. Click "Create New Role" button
2. Enter role name
3. Click "Submit"

**Edit Role**:
1. Click "Edit" button on role row
2. Modify name
3. Click "Submit"

**Delete Role**:
1. Click "Delete" button on role row
2. Confirm in dialog
3. Note: Deletion fails if role is assigned to users (error message displayed)

### User Operations

All existing User operations remain unchanged.

## Troubleshooting

### Port Conflicts

If port 80 or 4200 is in use:

```bash
# Check what's using the port (Windows)
netstat -ano | findstr :80

# Kill process by PID
taskkill /PID <pid> /F
```

### Container Logs

View logs for specific service:

```bash
docker-compose logs frontend
docker-compose logs app
docker-compose logs db
```

### Rebuild Without Cache

Force complete rebuild:

```bash
docker-compose build --no-cache
docker-compose up
```
