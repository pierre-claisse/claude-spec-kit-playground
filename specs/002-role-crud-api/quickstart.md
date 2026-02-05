# Quickstart: Role CRUD API

**Prerequisites**: Docker and Docker Compose installed

## Run the Application

```bash
# Build and start all containers (app + PostgreSQL)
docker-compose up --build

# Application available at http://localhost:8080
```

## Run Tests

```bash
# Run integration tests inside Docker (uses Testcontainers)
docker-compose -f docker-compose.test.yml run --rm test
```

## Stop the Application

```bash
docker-compose down

# To also remove the database volume:
docker-compose down -v
```

## API Usage Examples

### Role CRUD Operations

#### Create a Role

```bash
curl -X POST http://localhost:8080/roles \
  -H "Content-Type: application/json" \
  -d '{"name": "admin"}'
```

#### List All Roles

```bash
curl http://localhost:8080/roles
```

#### Get Role by ID

```bash
curl http://localhost:8080/roles/1
```

#### Update Role

```bash
curl -X PUT http://localhost:8080/roles/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "administrator"}'
```

#### Delete Role (only if not assigned to any user)

```bash
curl -X DELETE http://localhost:8080/roles/1
```

### User-Role Assignment Operations

#### List User's Roles

```bash
curl http://localhost:8080/users/1/roles
```

#### Assign Role to User

```bash
curl -X POST http://localhost:8080/users/1/roles/1
```

#### Remove Role from User

```bash
curl -X DELETE http://localhost:8080/users/1/roles/1
```

### User CRUD Operations (unchanged from v1)

#### Create a User

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'
```

#### List All Users

```bash
curl http://localhost:8080/users
```

#### Get User by ID

```bash
curl http://localhost:8080/users/1
```

#### Update User

```bash
curl -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Doe"}'
```

#### Delete User

```bash
curl -X DELETE http://localhost:8080/users/1
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| SPRING_DATASOURCE_URL | jdbc:postgresql://db:5432/usercrud | Database URL |
| SPRING_DATASOURCE_USERNAME | postgres | Database username |
| SPRING_DATASOURCE_PASSWORD | postgres | Database password |

## Project Structure

```
.
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── docker-compose.test.yml
└── src/
    ├── main/
    │   ├── java/com/example/usercrud/
    │   │   ├── UserCrudApplication.java
    │   │   ├── User.java                    # Modified: added roles relationship
    │   │   ├── UserRepository.java
    │   │   ├── UserController.java          # Modified: added user-role endpoints
    │   │   ├── Role.java                    # New: Role entity
    │   │   └── RoleRepository.java          # New: Role repository
    │   │   └── RoleController.java          # New: Role CRUD endpoints
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/example/usercrud/
            ├── UserControllerIntegrationTest.java
            └── RoleControllerIntegrationTest.java  # New: Role integration tests
```

## HTTP Status Codes

| Status | Meaning |
|--------|---------|
| 200 | OK - Request succeeded |
| 201 | Created - Resource created |
| 204 | No Content - Resource deleted |
| 400 | Bad Request - Invalid input (missing/empty required fields) |
| 404 | Not Found - Resource does not exist |
| 409 | Conflict - Duplicate name or role assigned to users |
