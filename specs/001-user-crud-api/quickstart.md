# Quickstart: User CRUD API

**Prerequisites**: Docker and Docker Compose installed

## Run the Application

```bash
# Build and start all containers (app + PostgreSQL)
docker-compose up --build

# Application available at http://localhost:8080
```

## Run Tests

```bash
# Run integration tests inside Docker
docker-compose run --rm app ./mvnw test
```

Or build and test in a single command:

```bash
docker build --target test -t user-crud-api-test .
```

## Stop the Application

```bash
docker-compose down

# To also remove the database volume:
docker-compose down -v
```

## API Usage Examples

### Create a User

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'
```

### List All Users

```bash
curl http://localhost:8080/users
```

### Get User by ID

```bash
curl http://localhost:8080/users/1
```

### Update User

```bash
curl -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Doe"}'
```

### Delete User

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
└── src/
    ├── main/
    │   ├── java/com/example/usercrud/
    │   │   ├── UserCrudApplication.java
    │   │   ├── User.java
    │   │   ├── UserRepository.java
    │   │   └── UserController.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/example/usercrud/
            └── UserControllerIntegrationTest.java
```
