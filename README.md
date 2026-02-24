# Spring Boot Task Management API

This project demonstrates a robust RESTful CRUD (Create, Read, Update, Delete) API for managing tasks, built with Java, Spring Boot, and Spring Data JPA. It incorporates modern best practices for clean architecture, testability, and deployability.

## Key Features & Improvements

- **Clean Architecture:** Structured into distinct layers (controller, service, repository, model, dto, exception, mapper) for improved maintainability and scalability.
- **Data Transfer Objects (DTOs):** Utilizes DTOs to decouple the API contract from the internal domain model and enhance security.
- **Custom API Response Envelope:** All API responses are wrapped in a consistent JSON structure (`statusCode`, `data`, `error`) for predictable client-side handling.
- **Input Validation:** Implements robust server-side validation using Jakarta Bean Validation to ensure data integrity for incoming requests.
- **Centralized Exception Handling:** Provides consistent, structured JSON error responses across the API using a global exception handler.
- **Custom Pagination & Sorting:** Supports custom query parameters (`offset`, `limit`, `sortBy`) for efficient data retrieval.
- **Structured Logging:** Enhanced application observability with meaningful log statements across service and controller layers.
- **Comprehensive Testing:** Includes both unit tests (for business logic) and integration tests (for API endpoints).
- **Dockerization:** Ready for containerized deployment with a provided `Dockerfile`.
- **Database:** Configured to connect to a PostgreSQL database.

## Prerequisites

Before you begin, ensure you have the following installed:

- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) (or higher)
- [Docker](https://www.docker.com/products/docker-desktop/) (for database or containerized application)
- [Maven](https://maven.apache.org/install.html) (or use the included Maven Wrapper `./mvnw`)

## Setup and Running the Application

### 1. Start the PostgreSQL Database

It is recommended to run PostgreSQL using Docker.

```bash
docker run --name local-postgres -e POSTGRES_DB=your_database -e POSTGRES_USER=your_username -e POSTGRES_PASSWORD=your_password -p 5432:5432 -d postgres:16
```

**Important:** Replace `your_database`, `your_username`, and `your_password` with your desired credentials. Remember these as you'll need them for `application.properties`.

### 2. Configure Database Connection

The project expects a `src/main/resources/application.properties` file for database configuration.

1. Copy the example file:

   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

2. Open `src/main/resources/application.properties` and fill in your PostgreSQL credentials to match the Docker command above:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### 3. Build and Run the Spring Boot Application

Navigate to the project root and use the Maven Wrapper:

**Build:**

```bash
./mvnw clean install
```

This will compile the application, run all tests, and package it into a JAR file.

**Run:**

```bash
./mvnw spring-boot:run
```

Alternatively, after building, you can run the JAR directly:

```bash
java -jar target/spring-boot-crud-api-0.0.1-SNAPSHOT.jar
```

The API server will start on `http://localhost:8080` (the default, or your configured port, see below).

### Port Configuration

The server port can be configured by adding a `server.port` property to `src/main/resources/application.properties`. For example:

```properties
server.port=XXXX # Replace XXXX with your desired port, e.g., 8081
```

## API Endpoints

The base URL for the API is `http://localhost:<port>/api/tasks`.

### `Task` Resource Endpoints

| Method   | Path              | Description                                                                         |
| :------- | :---------------- | :---------------------------------------------------------------------------------- |
| `POST`   | `/api/tasks`      | Creates a new task.                                                                 |
| `GET`    | `/api/tasks`      | Retrieves all tasks with optional title, completed status, pagination, and sorting. |
| `GET`    | `/api/tasks/{id}` | Retrieves a single task by its ID.                                                  |
| `PUT`    | `/api/tasks/{id}` | Updates an existing task by ID.                                                     |
| `PATCH`  | `/api/tasks/{id}` | Partially updates an existing task by ID.                                           |
| `DELETE` | `/api/tasks/{id}` | Deletes a task by its ID.                                                           |

### Filtering, Pagination and Sorting Parameters (for GET /api/tasks)

You can append the following query parameters to `GET /api/tasks`:

- `title`: (Optional) Filter tasks by title (case-insensitive substring match).
- `completed`: (Optional) Filter tasks by completion status (`true` or `false`).
- `offset`: (Optional) The starting index of the results (default: `0`).
- `limit`: (Optional) The maximum number of items to return (default: `20`).
- `sortBy`: (Optional) Sorting criteria in the format `property` (ascending) or `-property` (descending). Default sort is by `createdDate` ascending.

**Examples:**

- `GET http://localhost:8080/api/tasks?title=spring&completed=false&offset=0&limit=5&sortBy=title`
- `GET http://localhost:8080/api/tasks?completed=true&sortBy=-id`

### Request Body Examples

**POST <http://localhost:8080/api/tasks> (Create a new task):**

```json
{
  "title": "Learn Spring Boot",
  "completed": false
}
```

**PUT <http://localhost:8080/api/tasks/{id}> (Update an existing task):**

```json
{
  "title": "Master Spring Boot",
  "completed": true
}
```

**PATCH <http://localhost:8080/api/tasks/{id}> (Partially update an existing task):**

```json
{
  "title": "Only change title"
}
```

or

```json
{
  "completed": true
}
```

## API Response Structure

All API responses are wrapped in a standard JSON envelope for consistency.

### Successful Response (Single Item)

For `GET http://localhost:8080/api/tasks/{id}`, `POST http://localhost:8080/api/tasks`, `PUT http://localhost:8080/api/tasks/{id}`, `PATCH http://localhost:8080/api/tasks/{id}`. The `statusCode` will reflect the HTTP status (e.g., `200` for OK, `201` for Created).

```json
{
  "statusCode": 200,
  "data": {
    "id": 1,
    "title": "Learn Spring Boot",
    "completed": false,
    "active": true,
    "createdBy": "SYSTEM",
    "createdDate": "2026-02-24T10:30:00Z",
    "lastModifiedBy": "SYSTEM",
    "lastModifiedDate": "2026-02-24T10:30:00Z"
  },
  "error": null
}
```

### Successful Response (Paginated List)

For `GET http://localhost:8080/api/tasks`.

```json
{
  "statusCode": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "title": "Learn Spring Boot",
        "completed": false,
        "active": true,
        "createdBy": "SYSTEM",
        "createdDate": "2026-02-24T10:30:00Z",
        "lastModifiedBy": "SYSTEM",
        "lastModifiedDate": "2026-02-24T10:30:00Z"
      },
      {
        "id": 2,
        "title": "Write some tests",
        "completed": true,
        "active": true,
        "createdBy": "SYSTEM",
        "createdDate": "2026-02-24T10:35:00Z",
        "lastModifiedBy": "SYSTEM",
        "lastModifiedDate": "2026-02-24T10:35:00Z"
      }
    ],
    "total": 2
  },
  "error": null
}
```

### Error Response

For any failed request (e.g., validation error, item not found).

```json
{
  "statusCode": 404,
  "data": null,
  "error": {
    "message": "Task not found with id: 99",
    "detail": "org.springframework.web.server.ResponseStatusException: 404 NOT_FOUND \"Task not found with id: 99\""
  }
}
```

## Testing

The project includes comprehensive tests:

- **Unit Tests:** For `TaskService` (mocking `TaskRepository`).
- **Integration Tests:** For `TaskController` (using `MockMvc` and mocking `TaskService`).

To run all tests:

```bash
./mvnw clean install
```

## Dockerization

You can build and run the application as a Docker container.

### 1. Build the Docker Image

Navigate to the project root and run:

```bash
docker build -t spring-boot-task-api .
```

### 2. Run the Docker Container

Ensure your PostgreSQL container is running and accessible (as per "Setup and Running the Application" section). Then run:

```bash
docker run -p 8080:8080 spring-boot-task-api
```

Your API will be available at `http://localhost:8080` (or the port you mapped, make sure to adjust the `-p` flag if you use a different port).
