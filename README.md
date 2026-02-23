# Spring Boot Task Management API

This project demonstrates a robust RESTful CRUD (Create, Read, Update, Delete) API for managing tasks, built with Java, Spring Boot, and Spring Data JPA. It incorporates modern best practices for clean architecture, testability, and deployability.

## Key Features & Improvements

*   **Clean Architecture:** Structured into distinct layers (controller, service, repository, model, dto, exception, mapper) for improved maintainability and scalability.
*   **Data Transfer Objects (DTOs):** Utilizes DTOs (`CreateTaskRequest`, `UpdateTaskRequest`, `TaskResponse`, `ErrorResponse`) to decouple the API contract from the internal domain model and enhance security.
*   **Input Validation:** Implements robust server-side validation using Jakarta Bean Validation to ensure data integrity for incoming requests.
*   **Centralized Exception Handling:** Provides consistent, structured JSON error responses across the API using a global exception handler.
*   **Pagination & Sorting:** Efficiently retrieves and presents large datasets with built-in support for pagination and dynamic sorting.
*   **Structured Logging:** Enhanced application observability with meaningful log statements across service and controller layers.
*   **Comprehensive Testing:** Includes both unit tests (for business logic in services) and integration tests (for API endpoints via MockMvc).
*   **Dockerization:** Ready for containerized deployment with a provided `Dockerfile`.
*   **Database:** Configured to connect to a PostgreSQL database.

## Prerequisites

Before you begin, ensure you have the following installed:

*   [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) (or higher)
*   [Docker](https://www.docker.com/products/docker-desktop/) (for database or containerized application)
*   [Maven](https://maven.apache.org/install.html) (or use the included Maven Wrapper `./mvnw`)

## Setup and Running the Application

### 1. Start the PostgreSQL Database

It is recommended to run PostgreSQL using Docker.
```bash
docker run --name local-postgres -e POSTGRES_DB=your_database -e POSTGRES_USER=your_username -e POSTGRES_PASSWORD=your_password -p 5432:5432 -d postgres:16
```
**Important:** Replace `your_database`, `your_username`, and `your_password` with your desired credentials. Remember these as you'll need them for `application.properties`.

### 2. Configure Database Connection

The project expects a `src/main/resources/application.properties` file for database configuration.

1.  Copy the example file:
    ```bash
    cp src/main/resources/application.properties.example src/main/resources/application.properties
    ```
2.  Open `src/main/resources/application.properties` and fill in your PostgreSQL credentials to match the Docker command above:
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
java -jar target/demo-0.0.1-SNAPSHOT.jar
```
The API server will start on `http://localhost:8080`.

## API Endpoints

The base URL for the API is `http://localhost:8080/api/tasks`.

### `Task` Resource Endpoints

| Method   | Path              | Description                                                         | Request Body Example (for POST/PUT)                          |
| :------- | :---------------- | :------------------------------------------------------------------ | :----------------------------------------------------------- |
| `POST`   | `/api/tasks`      | Creates a new task.                                                 | `{"title":"Learn Spring Boot", "completed":false}`           |
| `GET`    | `/api/tasks`      | Retrieves all tasks with optional title, completed status, pagination, and sorting. | (None)                                                       |
| `GET`    | `/api/tasks/{id}` | Retrieves a single task by its ID.                                  | (None)                                                       |
| `PUT`    | `/api/tasks/{id}` | Updates an existing task by ID.                                     | `{"title":"Master Spring Boot", "completed":true}`           |
| `DELETE` | `/api/tasks/{id}` | Deletes a task by its ID.                                           | (None)                                                       |

### Filtering, Pagination and Sorting Parameters (for GET /api/tasks)

You can append the following query parameters to `GET /api/tasks`:
*   `title`: (Optional) Filter tasks by title (case-insensitive substring match).
*   `completed`: (Optional) Filter tasks by completion status (`true` or `false`).
*   `page`: (Optional) Zero-based page index (default: `0`).
*   `size`: (Optional) Number of items per page (default: `20`).
*   `sort`: (Optional) Sorting criteria in the format `property,(asc|desc)`. Default sort direction is ascending. Multiple sort criteria are supported (e.g., `sort=title,asc&sort=id,desc`).

**Examples:**
*   `GET /api/tasks?title=spring&completed=false&page=0&size=5&sort=title,asc`
*   `GET /api/tasks?completed=true`
*   `GET /api/tasks?sort=id,desc`

## Testing

The project includes comprehensive tests:

*   **Unit Tests:** For `TaskService` (mocking `TaskRepository`).
*   **Integration Tests:** For `TaskController` (using `MockMvc` and mocking `TaskService`).

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
Your API will be available at `http://localhost:8080`.

## Next Steps (Potential Further Improvements)

*   **Security:** Implement authentication (e.g., JWT) and authorization.
*   **More Advanced Logging:** Centralize logs with ELK stack, structured JSON logging.
*   **API Documentation:** Integrate Swagger/OpenAPI for interactive API documentation.
*   **Database Migrations:** Use Flyway or Liquibase for version-controlled database schema changes.
*   **Error Handling Refinement:** More granular custom exceptions.
*   **Performance Optimization:** Caching, database query optimization.
