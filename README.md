# Spring Boot CRUD API Demo

This is a simple demonstration project of a RESTful CRUD (Create, Read, Update, Delete) API built with Java, Spring Boot, and Spring Data JPA The API is connected to a PostgreSQL database running in Docker.

## Features

- Full CRUD functionality for a "Task" resource.
- Uses Spring Data JPA for database interaction.
- Connects to a PostgreSQL database.
- Built with Maven.

## Prerequisites

Before you begin, ensure you have the following installed:

- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) (or higher)
- [Docker](https://www.docker.com/products/docker-desktop/)

## Setup and Installation

1. **Clone the repository (if applicable)**

   ```bash
   git clone <your-repo-url>
   cd demo
   ```

2. **Start the PostgreSQL Database with Docker**

   Run the following command to start a PostgreSQL container with the required configuration:

   ```bash
   docker run --name local-postgres -e POSTGRES_DB=local -e POSTGRES_USER=developer -e POSTGRES_PASSWORD=developer -p 5432:5432 -d postgres:16
   ```

   To check if the container is running, use `docker ps`.

3. **Run the Spring Boot Application**

You can run the application using the included Maven Wrapper: ./mvnw spring-boot:run

The API server will start on `http://localhost:8080`.

## API Endpoints

The base URL for the API is `http://localhost:8080/api/tasks`.

| Method   | Path              | Description                        | Request Body Example                          |
| :------- | :---------------- | :--------------------------------- | :-------------------------------------------- |
| `POST`   | `/api/tasks`      | Creates a new task.                | `{"title":"My New Task", "completed":false}`  |
| `GET`    | `/api/tasks`      | Retrieves all task.                | (None)                                        |
| `GET`    | `/api/tasks/{id}` | Retrieves a single task by its ID. | (None)                                        |
| `PUT`    | `/api/tasks/{id}` | Updates an existing task.          | `{"title":"Updated Title", "completed":true}` |
| `DELETE` | `/api/tasks/{id}` | Updates an existing task.          | `{"title":"Updated Title", "completed":true}` |

### Example cURL Commands

**Create a Task:** curl -X POST <http://localhost:8080/api/tasks> \ -H "Content-Type: application/json" \ -d '{"title":"Test the API","completed":false}'

**Get All Tasks:** curl <http://localhost:8080/api/tasks>

**Update Task with ID 1:** curl -X PUT <http://localhost:8080/api/tasks/1> \ -H "Content-Type: application/json" \ -d '{"title":"Updated Title","completed":true}'

**Delete Task with ID 1:** curl -X DELETE <http://localshot:8080/api/tasks/1>
