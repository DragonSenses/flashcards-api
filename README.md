# flashcards-api
A modular and well-documented RESTful backend for managing flashcard-based study sessions. Built with Java, Spring Boot, Swagger UI.

# High Level Architecture

### **Layers**

![high-level-architecture diagram](docs/high-level-architecture.svg)

This diagram represents a clean **separation of concerns** across the backend architecture:

- **Client / Swagger UI**: Initiates HTTP requests and interacts with documented API endpoints
- **Controller Layer**: Accepts HTTP requests and coordinates interaction with services
- **Service Layer**: Encapsulates business logic, including validation and exception generation
- **DTOs & Mappers**: Facilitate data transformation between domain entities and API contracts
- **Repository Layer**: Interfaces with persistent storage (e.g., JPA repositories for `Category`)
- **MySQL Database**: Stores and retrieves persistent data in structured relational format

### Error Handling Flow

The diagram below illustrates how exceptions propagate through the service layer and are transformed into structured HTTP responses:

![Error Handling Diagram](docs/error-handling.svg)

- Custom exceptions (`NotFoundException`, `ConflictException`, `BadRequestException`) are thrown from services.
- `GlobalExceptionHandler` intercepts these using `@ControllerAdvice` and maps them to `ErrorResponse`.
- Validation failures (`MethodArgumentNotValidException`) are transformed via `ValidationErrorExtractor` into a standardized payload.
- All errors are returned to the client as structured JSON with appropriate HTTP status codes.

# Technologies:
Java 17, Spring Boot, Spring MVC, Spring JPA, Spring Web

# Prerequisites
1) JDK / Java 17
2) MySQL

# Specifications

- User can **create**, **read**, **update** and **delete** *Categories*.
- User can **create**, **read**, **update** and **delete** *Study Sessions*.
- User can **create**, **read**, **update** and **delete** *Flashcards*.

# Instructions to run locally

1. Clone this repo (or download zip on GitHub).
2. Go to the directory the files are located.
3. Create the MySQL schema manually:

```sql
CREATE DATABASE flashcards;
```

1) Inside the folder `src/resources/application.yml` file change the properties in datasource to match your local MySQL credentials for `username` and `password`: 
`spring.datasource.username` and `spring.datasource.password`

```application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/flashcards
    username: yourUsername
    password: yourPassword
```

4) Run the application
5) After running the application, test it manually with this tool in the following path:

```sh
http://localhost:8080/swagger-ui/index.html
```