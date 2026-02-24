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

This project is configured to run with Docker Compose, which simplifies the setup of the application and the PostgreSQL database.

### 1. Start the Application and Database

Navigate to the project root and run the following command:

```bash
docker-compose up --build
```

This single command will:

1.  Build the Docker image for the Spring Boot application.
2.  Start a PostgreSQL database container.
3.  Start the application container, preconfigured to connect to the database.

The API server will start on `http://localhost:8080`.

### 2. Stop the Application

To stop all the services, press `Ctrl+C` in the terminal where the services are running, and then run:

```bash
docker-compose down
```

This will stop and remove the containers defined in the `docker-compose.yml` file.

## API Documentation

With the application running, you can access the interactive API documentation (Swagger UI) at the following URL:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## API Endpoints

The base URL for the API is `http://localhost:<port>/api/tasks`.

## Postman Collection

You can import the Postman collection for this API using the following link:

[Postman Collection](https://.postman.co/workspace/My-Workspace~b5610688-11dc-4c8b-8a9c-e36a3da34314/collection/11327112-859b91eb-4b52-4ac1-a42b-e34448f77d71?action=share&creator=11327112&active-environment=11327112-4f33c8e8-fa28-49b5-a445-580f67357cdb)

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
