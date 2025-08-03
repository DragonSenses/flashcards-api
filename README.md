# flashcards-api
A modular and well-documented RESTful backend for managing flashcard-based study sessions. Built with Java and Spring Boot, featuring OpenAPI documentation via Swagger UI.

Work in Progress:
- [x] DTO
- [x] Mapper
- [x] Repo
- [x] Exception
- [x] Service
- [x] Controller
- [x] Mock SQL Test Data
- [x] Unit Tests
  - [x] CategoryService
  - [x] StudySessionService
  - [x] FlashcardService
  - [x] Mapper null-safe transformations
  - [x] DTO validation logic
- [ ] Controller Tests (`@WebMvcTest`, `MockMvc`)
- [ ] Integration Tests (`@SpringBootTest`)
  - [ ] Swagger UI
  - [ ] Service to Repository Integration
  - [ ] Controller to Service Integration
  - [ ] etc. (Validation, Exception, DTO Mapper)

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

- **Language & Runtime:** Java 17
- **Frameworks:** Spring Boot, Spring MVC, Spring Data JPA, Spring Web
- **Data Persistence:** MySQL
- **Documentation:** SpringDoc OpenAPI, Swagger UI
- **Build Tool:** Maven

# Prerequisites
1. Java 17+ (JDK installed and available in PATH)  
2. MySQL Server running locally with default port (3306) 

# Specifications

- Users can **create**, **read**, **update**, and **delete** *Categories* via REST endpoints.
- Users can **manage** *Study Sessions* tied to specific categories.
- Users can **create**, **search**, and **delete** *Flashcards* linked to study sessions.

- **Category Management:**  
  - `GET /api/v1/categories`  
  - `POST /api/v1/categories`  
  - `PUT /api/v1/categories`  
  - `DELETE /api/v1/categories/{id}`

- **Study Session Management:**  
  - CRUD + `GET /sessions/category/{categoryId}`

- **Flashcard Operations:**  
  - CRUD + `GET /flashcards/session/{studySessionId}`

# Instructions to Run Locally

1. **Clone the repository** (or download ZIP from GitHub)

   ```sh
   git clone https://github.com/your-username/flashcards-api.git
   cd flashcards-api
   ```

2. **Create the MySQL schema manually**

   Ensure MySQL is running locally, then execute:

   ```sql
   CREATE DATABASE flashcards;
   ```

3. **Configure database credentials**

   Update `src/resources/application.yml` to match your local MySQL settings:

   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/flashcards
       username: yourUsername
       password: yourPassword
   ```

4. **Run the application**

   Use Maven wrapper (or your IDE):

   ```sh
   ./mvnw spring-boot:run
   ```

5. **Test the API manually**

   Once running, visit the Swagger UI for interactive documentation:

   ```
   http://localhost:8080/swagger-ui/index.html
   ```