# Architectural Decision Log

This document captures key architectural decisions made during the development of the Flashcards API — a modular and well-documented RESTful backend for managing flashcard-based study sessions. Built with Java and Spring Boot, featuring OpenAPI documentation via Swagger UI.

---

## ADR #001: Use Spring Boot as the Backend Framework
**Date**: 2025-07-18
**Status**: Accepted
**Context**: Need for rapid API development with built-in dependency management
**Decision**: Adopt Spring Boot
**Consequences**: Accelerated development cycle; convention over configuration

---

## ADR #002: Layered Architecture with DTOs and Service Classes
**Date**: 2025-07-18
**Status**: Accepted
**Context**: Maintain separation of concerns
**Decision**: Introduce DTOs, mappers, and service layer
**Consequences**: Easier testing and modularity

---

## ADR #003: Add JPA Repository  
**Date**: 2025-07-21  
**Status**: Accepted  
**Context**: Data persistence and retrieval were needed for Flashcard and StudySession entities. Manual JDBC or lower-level access would hinder scalability and consistency.  
**Decision**: Introduce Spring Data JPA repositories for entity access abstraction.  
**Consequences**: Enables declarative data access, custom queries via method naming, and integration with Hibernate ORM out-of-the-box.

---

## ADR #004: Exceptions and Error Handling  
**Date**: 2025-07-21  
**Status**: Accepted  
**Context**: Flashcard API needed a robust way to handle invalid input, missing resources, and system-level failures with standardized error messages.  
**Decision**: Implement a centralized error handling mechanism using custom exceptions, `ErrorResponse` DTO, and a `GlobalExceptionHandler`.  
**Consequences**: Improved API clarity, consistent error responses, better Swagger documentation, and more maintainable exception flow.

---

## ADR #005: Controllers  
**Date**: 2025-07-23  
**Status**: Accepted  
**Context**: The API required clear entry points for HTTP operations with a well-documented and testable controller structure.  
**Decision**: Build REST controllers using Spring annotations, DTO mapping, and semantic method naming. Use Swagger annotations for documentation.  
**Consequences**: Clean separation between transport and business logic, improved API discoverability via Swagger UI, and reusable response patterns using a `ResponseHandler` interface.

---

## ADR #00: 
**Date**: 2025-07-18
**Status**: Proposed / Accepted / Deprecated
**Context**: 
**Decision**: 
**Consequences**: 


## Development Approach

This project adheres to the Model-View-Controller (MVC) design pattern:
- **Model**: DTOs and domain models representing flashcards, study sessions, and user interactions.
- **View**: Exposed through Swagger UI, serving as an interactive documentation layer.
- **Controller**: RESTful endpoints that orchestrate business logic, validation, and exception handling.

This ADR aims to formalize architectural choices and facilitate onboarding for future contributors.

## Context

The application serves study session tracking with a need for modularity, maintainability, and clear API boundaries. The primary goal is to support scalable feature additions like spaced repetition algorithms or real-time collaboration.

## Decision

Use Spring Boot for rapid REST API scaffolding, along with:
- Swagger/OpenAPI for API documentation
- DTO-mapper-service layering to isolate concerns
- Custom exception handling for robust error management

## Consequences

- Rapid onboarding due to clear layer separation
- Easier monitoring and debugging via structured logs
- Simplified testing pipelines with mocks at service/controller boundaries

### Application Structure

Create the following package structure under `src/main/java/com/ken/flashcards`:

```
├── controller
├── dto
├── error
└── exception
├── mapper
├── model
├── repository
├── service
```

### Bootstrap with **Spring Initializr**

Using **Spring Initializr** is an excellent way to bootstrap the project — fast, clean, and Eclipse-friendly.

#### Use the Web Interface

1. Go to [https://start.spring.io](https://start.spring.io)  
2. Set the following options:
   - **Project**: Maven
   - **Language**: Java
   - **Spring Boot version**: 3.x (latest stable)
   - **Group**: `com.ken`
   - **Artifact**: `flashcards-api`
   - **Name**: `FlashcardsApi`
   - **Package Name**: `com.ken.flashcards`
   - **Packaging**: Jar
   - **Java Version**: 17+

3. Click **“Add Dependencies”** and select:
   - Spring Web
   - Spring Data JPA
   - Validation
   - MySQL Driver
   - Lombok (optional)
   - SpringDoc OpenAPI (Swagger)

4. Click **Generate**, download the `.zip`, and unzip locally.

5. Add more dependencies to `pom.xml`

```xml
    <!-- Swagger-style API documentation (Add this) -->
    <dependency>
      <groupId>org.springdoc</groupId>
      <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
      <version>2.0.2</version>
    </dependency>

    <!-- MySQL connector for database communication (Add this) -->
    <dependency>
      <groupId>com.mysql</groupId>
      <artifactId>mysql-connector-j</artifactId>
      <scope>runtime</scope>
    </dependency>
```

6. In Eclipse:
   - **File → Import → Existing Maven Projects**
   - Select the unzipped folder
   - Eclipse will auto-recognize `pom.xml` and set up the build path

# 🧬 Domain Model Overview

The app uses three core entities: `Category`, `StudySession`, and `Flashcard`.

Each is a JPA entity with validation constraints and Lombok annotations for boilerplate reduction.

We'll lay the foundation for **JPA persistence** and **REST exposure**.

### 📦 Package Location
All entities are located in:
```
com.ken.flashcards.model
```

### 📘 1. `Category.java`

```java
@Entity
public class Category {
    @Id
    @NotBlank(message = "id is required")
    private final String id;

    @NotBlank(message = "name is required")
    private final String name;
}
```

- Represents a study topic or subject
- Fields:
  - `id`: Unique identifier
  - `name`: Name of the category
- Uses `@Entity` for JPA mapping
- Validation via `@NotBlank`
- Lombok: `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`

---

### 📘 2. `StudySession.java`

```java
@Entity
public class StudySession {
    @Id
    @NotBlank(message = "id is required")
    private final String id;

    @NotBlank(message = "category id is required")
    private final String categoryId;

    @NotBlank(message = "name is required")
    private final String name;
}
```

- Represents a specific study session under a category
- Fields:
  - `id`: Unique identifier
  - `categoryId`: Foreign key reference to `Category`
  - `name`: Session name
- No explicit JPA relationship (`@ManyToOne`) — uses manual ID linking

---

### 📘 3. `Flashcard.java`

```java
@Entity
public class Flashcard {
    @Id
    @NotBlank(message = "id is required")
    private final String id;

    @NotBlank(message = "study session id is required")
    private final String studySessionId;

    @NotBlank(message = "question is required")
    private final String question;

    @NotBlank(message = "answer is required")
    private final String answer;
}
```

- Represents a single flashcard
- Fields:
  - `id`: Unique identifier
  - `studySessionId`: Foreign key reference to `StudySession`
  - `question`: The prompt
  - `answer`: The response
- Again, uses manual ID linking rather than JPA relationships

## 🧠 Design Notes

- I opted for **manual foreign key linking** (`String` IDs) rather than JPA relationships (`@ManyToOne`, `@JoinColumn`). This simplifies serialization and avoids lazy loading issues but requires manual integrity checks.
- Use of `final` fields with Lombok constructors enforces immutability.
- Validation annotations (`@NotBlank`) ensure data integrity at the API level.

# **DTO (Data Transfer Object)**

> DTOs define the contract between your controller and service logic. They clarify what data gets passed in and returned, and they shape your validation rules (`@NotBlank`, `@Size`, etc.)

In Java Spring Boot, a **DTO (Data Transfer Object)** is a design pattern used to transfer data between different layers of an application, such as the controller, service, and repository layers. It is particularly useful for encapsulating data and ensuring that only the necessary information is exposed or passed around, improving security, performance, and maintainability.

#### Key Features of DTO:
- **Encapsulation**: DTOs encapsulate data, often representing a subset of fields from an entity or combining fields from multiple entities.
- **Decoupling**: They decouple the internal domain models (e.g., JPA entities) from the external API or client, preventing overexposure of sensitive or unnecessary data.
- **Validation**: DTOs can include validation annotations to ensure data integrity when receiving input from clients.
- **Serialization**: DTOs are often serialized into JSON or XML when interacting with APIs.

#### Benefits of Using DTOs:
- **Security**: Prevents exposing sensitive fields (e.g., passwords) in API responses.
- **Flexibility**: Allows customizing the data structure sent to clients without modifying the underlying entity.
- **Performance**: Reduces the amount of data transferred over the network by including only relevant fields.
- **Separation of Concerns**: Keeps the domain model focused on business logic while the DTO handles data representation.

---

# ✉️ DTO Overview

The application uses data transfer objects (DTOs) to encapsulate input payloads for resource creation. These classes separate internal domain logic from exposed API contracts and include validation constraints for safer API consumption.

Each DTO is defined as an immutable class using Lombok, and they're located in:

```
com.ken.flashcards.dto
```

---

## 📘 1. `CategoryRequest.java`

```java
@Data
@NoArgsConstructor(force = true, access = PRIVATE)
@AllArgsConstructor
public class CategoryRequest {

  @NotBlank(message = "name is required")
  private final String name;

}
```

- Represents a request to create a new `Category`
- Field:
  - `name`: required name of the category
- Uses `@NotBlank` to ensure form submission integrity
- Implements immutability through `final` fields with Lombok constructors

---

## 📘 2. `StudySessionRequest.java`

```java
@Data
@NoArgsConstructor(force = true, access = PRIVATE)
@AllArgsConstructor
public class StudySessionRequest {

  @NotBlank(message = "category id is required")
  private final String categoryId;

  @NotBlank(message = "name is required")
  private final String name;

}
```

- Represents creation input for a `StudySession`
- Fields:
  - `categoryId`: string reference to an existing `Category`
  - `name`: session name
- Designed to validate relationships without enforcing database joins
- Relies on manual foreign key linking

---

## 📘 3. `FlashcardRequest.java`

```java
@Data
@NoArgsConstructor(force = true, access = PRIVATE)
@AllArgsConstructor
public class FlashcardRequest {

  @NotBlank(message = "study session id is required")
  private final String studySessionId;

  @NotBlank(message = "question is required")
  private final String question;

  @NotBlank(message = "answer is required")
  private final String answer;

}
```

- Payload model for flashcard creation
- Fields:
  - `studySessionId`: parent reference to a `StudySession`
  - `question`: the prompt text
  - `answer`: the response text
- Ensures data consistency via `@NotBlank` constraints

---

## 🧠 Design Notes

- **Field validation**: All fields include validation annotations for early failure detection at the controller layer.
- **Immutability**: Each DTO uses `final` fields with forced private no-arg constructors to support deserialization while maintaining object integrity.
- **Separation of concerns**: These DTOs are not entities. They decouple API contracts from internal persistence, simplifying refactors and mapper logic.
- **Manual linking**: Foreign keys (`categoryId`, `studySessionId`) are passed as strings — aligning with the domain model's design philosophy.

---

# 🔁 Mapper Layer Overview

The app uses dedicated mapper classes to transform incoming DTOs into JPA entities. This promotes clear separation between API contracts and domain models, reinforces immutability, and prepares the data for persistence.

Each mapper is defined by an interface and corresponding implementation. They rely on a shared utility — `IdGenerator` — to assign unique identifiers when creating entities from DTOs.

### 📦 Package Location  
All mappers are located in:
```
com.ken.flashcards.mapper
```

---

### 🧭 Design Summary

| Component                | Role                                       |
|--------------------------|--------------------------------------------|
| `CategoryMapper`         | Converts `CategoryRequest` → `Category`    |
| `FlashcardMapper`        | Converts `FlashcardRequest` → `Flashcard`  |
| `StudySessionMapper`     | Converts `StudySessionRequest` → `StudySession` |
| `IdGenerator`            | Generates unique `String` IDs for entities |
| `IdGeneratorImpl`        | UUID-based implementation of `IdGenerator` |

Each mapper is annotated with `@Component` to enable Spring-managed injection and is structured around constructor-based dependency injection of `IdGenerator`.

---

### ⚙️ Example: `FlashcardMapperImpl`

```java
@Component
public class FlashcardMapperImpl implements FlashcardMapper {

  private final IdGenerator idGenerator;

  public FlashcardMapperImpl(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  public Flashcard flashcardFrom(FlashcardRequest request) {
    return new Flashcard(
      idGenerator.generateId(),
      request.getStudySessionId(),
      request.getQuestion(),
      request.getAnswer()
    );
  }
}
```

---

## 🧠 Design Notes

- 🔁 **One-way mapping only**: Each mapper focuses exclusively on DTO-to-entity conversion for creation workflows. Bidirectional mapping (e.g., entity → response DTO) can be introduced later if needed.
- 🧪 **Testable by design**: Mapper implementations are stateless and rely on injected collaborators (`IdGenerator`), making them ideal targets for unit tests.
- 🔗 **Manual ID injection**: Instead of generating IDs in the controller or service layers, mappers encapsulate this logic for better cohesion.
- 📏 **Interface-based structure**: Keeping mappers as interfaces allows for easier swapping with tools like MapStruct or ModelMapper if the project evolves.

---

# 🗄️ Repository Layer Overview

The application uses Spring Data JPA repositories to abstract database access and simplify persistence logic. Each repository is an interface that extends `JpaRepository`, providing CRUD methods out of the box without requiring implementation.

Repositories are located in:

```
com.ken.flashcards.repository
```

---

### 📘 Defined Interfaces

| Repository Interface             | Associated Entity    | ID Type |
|----------------------------------|----------------------|---------|
| `CategoryRepository`             | `Category`           | `String` |
| `StudySessionRepository`         | `StudySession`       | `String` |
| `FlashcardRepository`            | `Flashcard`          | `String` |

Each repository interface inherits from:

```java
JpaRepository<EntityType, String>
```

This enables:
- `save()`, `findById()`, `deleteById()`, etc.
- Pagination, sorting, and custom query support

---

### 🧠 Design Notes

- 🧩 **Layer separation**: Repositories encapsulate persistence logic and prevent service classes from touching the EntityManager directly.
- 🔁 **UUID keys**: All entities use `String` identifiers generated via `IdGenerator`, ensuring consistent usage across repositories.
- ⚡ **Zero-boilerplate CRUD**: Spring Data JPA provides full CRUD and paging methods without manual implementation.
- 🔍 **Future extensibility**: Custom queries (e.g. `List<Flashcard> findByStudySessionId(...)`) can be added later without breaking the service layer.
- 🧪 **Testable logic**: Repositories can be mocked or bootstrapped with H2/Testcontainers for integration testing.

# 🚨 Exception Layer Overview

The exception layer encapsulates error signaling and response mapping across services and controllers. It defines custom runtime exceptions tailored to common failure scenarios such as missing resources, invalid inputs, and conflicting operations.

All exception classes reside in:

```
com.ken.flashcards.exception
```

---

### 📘 Defined Exceptions

| Exception Class         | Purpose                                      |
|-------------------------|----------------------------------------------|
| `NotFoundException`     | Indicates missing entities or invalid IDs    |
| `BadRequestException`   | Signals malformed or invalid request data    |
| `ConflictException`     | Represents duplicate or conflicting resources |

---

### 🔍 Usage Pattern

Custom exceptions are thrown within service methods like:

```java
return repository.findById(id)
    .orElseThrow(() -> new NotFoundException("Flashcard not found with ID: " + id));
```

And can be caught at the controller level or handled globally via a `RestControllerAdvice` class.

---

## 🧠 Design Notes

- 🎯 **Semantic errors**: Each exception expresses a specific failure mode, improving clarity for client-side consumption.
- 📦 **Layer decoupling**: Exceptions are centrally defined, enabling reuse across services, controllers, and future validators.
- 🧪 **Testability**: Service methods can be unit-tested to assert that exceptions are thrown under edge cases.
- 📤 **REST compliance**: Supports clean HTTP status mapping (e.g., 404 for not found, 400 for bad request, 409 for conflict).
- 🛡️ **Extensible structure**: Future additions (e.g., `UnauthorizedException`) can plug into the same package and handling mechanism.

# ✅ Validation Utility Overview

The application uses a centralized validation helper class, `ValidatingService`, to streamline null checks, field validation, and throw semantic exceptions when inputs fail integrity rules.

This utility resides in:

```
com.ken.flashcards.service.ValidatingService
```

It is extended by service implementations (e.g., `FlashcardServiceImpl`) to ensure all service-level input handling remains consistent, modular, and readable.

---

### 📘 Methods

| Method                               | Purpose                                       |
|--------------------------------------|-----------------------------------------------|
| `assertNotNull(Object obj)`          | Validates that an input object is non-null    |
| `assertNotBlank(String value, name)` | Validates that a field string is not blank    |

These methods throw appropriate exceptions (e.g., `BadRequestException`) when validation fails, which are later translated to HTTP 400 responses by a global exception handler.

---

## 🧠 Design Notes

- 🧩 **Cross-service consistency**: All service implementations share uniform input checks
- 🔁 **Exception-driven flow**: Failures are surfaced via custom exceptions rather than silent failure or null returns
- 🛡️ **Extensible base class**: Future validation methods (e.g., `assertIdFormat`, `assertEmailPattern`) can be added without affecting service signatures
- 🧪 **Isolated testability**: Validation logic is decoupled and can be unit-tested independently from service orchestration
- 📦 **Layer-fit placement**: Although functionally a utility, it’s scoped under `service` to reflect its direct role in service-layer input hygiene

# 🛎️ Service Interface Overview

The `FlashcardService` interface defines application-level operations for managing `Flashcard` resources. It abstracts business logic behind a clear, injectable contract, promoting separation of concerns and testability.

This interface resides in:

```
com.ken.flashcards.service
```

---

### 📘 `FlashcardService.java`

```java
public interface FlashcardService {

  Iterable<Flashcard> findAll();

  Flashcard findById(String id);

  Flashcard createFlashcard(FlashcardRequest request);

  boolean existsById(String id);

  Flashcard save(Flashcard flashcard);

  void deleteById(String id);

  Iterable<Flashcard> findAllByStudySessionId(String studySessionId);
}
```

---

### 🔍 Method Overview

| Method                         | Purpose                                   |
|--------------------------------|--------------------------------------------|
| `findAll()`                    | Retrieves all flashcards                  |
| `findById(String id)`          | Finds a flashcard by its unique ID        |
| `createFlashcard(request)`     | Converts DTO to entity and persists it    |
| `existsById(String id)`        | Checks existence of a flashcard ID        |
| `save(flashcard)`              | Persists an entity (external or updated)  |
| `deleteById(String id)`        | Deletes a flashcard by ID                 |
| `findAllByStudySessionId(id)`  | Retrieves flashcards linked to a session  |

---

## 🧠 Design Notes

- 🔁 **Interface-first design**: Promotes flexibility; implementation can evolve without breaking API dependencies.
- 🔧 **DTO-driven creation**: Uses `FlashcardRequest` to abstract external input and enforce validation.
- 🧩 **Repository delegation**: Each method orchestrates a call to `FlashcardRepository` after applying logic and mapping.
- 📏 **Consistency**: Method names follow conventional Spring patterns (`findBy`, `deleteBy`, `save`) for intuitive comprehension.
- 🧪 **Mock-friendly**: Easily mockable in unit tests via interface injection, simplifying controller/service boundary testing.

# 🛎️ Service Layer Overview

The service layer coordinates application logic by mediating between controllers, mappers, and repositories. It centralizes business rules, orchestrates entity creation, and manages persistence through injected dependencies.

Services are defined as Spring `@Service` components and reside in:

```
com.ken.flashcards.service
```

---

### 📘 Defined Services

| Service Class              | Responsibility                        |
|----------------------------|----------------------------------------|
| `CategoryService`          | Create and query category resources   |
| `StudySessionService`      | Create sessions linked to categories |
| `FlashcardService`         | Create and retrieve flashcards tied to sessions |

---

### ⚙️ Example: `FlashcardService`

```java
@Service
public class FlashcardService {

  private final FlashcardRepository repository;
  private final FlashcardMapper mapper;

  public FlashcardService(FlashcardRepository repository, FlashcardMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  public Flashcard create(FlashcardRequest request) {
    Flashcard flashcard = mapper.flashcardFrom(request);
    return repository.save(flashcard);
  }

  public Iterable<Flashcard> findBySession(String sessionId) {
    return repository.findAllByStudySessionId(sessionId);
  }
}
```

---

## 🧠 Design Notes

- 🧩 **Separation of concerns**: The service layer isolates business operations from web handling and data persistence.
- 🔁 **Mapper integration**: DTOs are transformed using injected mappers before repository calls.
- 🧪 **Testable orchestration**: Services can be unit-tested by mocking mappers and repositories independently.
- 📤 **Controller-friendly APIs**: Service methods are structured to be directly consumable by REST controllers.
- 🛡️ **Optional validation**: Business rule enforcement and integrity checks (e.g. uniqueness) can be placed here.

## FlashCardServiceImpl

```java
package com.ken.flashcards.service.impl;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ken.flashcards.dto.FlashcardRequest;
import com.ken.flashcards.exception.NotFoundException;
import com.ken.flashcards.mapper.FlashcardMapper;
import com.ken.flashcards.model.Flashcard;
import com.ken.flashcards.repository.FlashcardRepository;
import com.ken.flashcards.service.FlashcardService;
import com.ken.flashcards.service.StudySessionService;
import com.ken.flashcards.service.ValidatingService;

@Service
@Transactional
public class FlashcardServiceImpl extends ValidatingService implements FlashcardService {

  private final FlashcardRepository repository;
  private final StudySessionService studySessionService;
  private final FlashcardMapper mapper;

  @Autowired
  public FlashcardServiceImpl(FlashcardRepository repository,
      StudySessionService studySessionService, FlashcardMapper mapper) {
    this.repository = repository;
    this.studySessionService = studySessionService;
    this.mapper = mapper;
  }

  @Override
  public Iterable<Flashcard> findAll() {
    return repository.findAll();
  }

  @Override
  public Flashcard findById(String id) {
    return repository.findById(id)
        .orElseThrow(() -> new NotFoundException(format("Cannot find flashcard with id = %s", id)));
  }

  @Override
  public Flashcard createFlashcard(FlashcardRequest request) {
    validate(request);
    Flashcard flashcard = flashcardFrom(request);
    return repository.save(flashcard);
  }

  @Override
  public boolean existsById(String id) {
    return repository.existsById(id);
  }

  @Override
  public Flashcard save(Flashcard flashcard) {
    validate(flashcard);
    return repository.save(flashcard);
  }

  @Override
  public void deleteById(String id) {
    repository.deleteById(id);
  }

  @Override
  public Iterable<Flashcard> findAllByStudySessionId(String studySessionId) {
    studySessionService.assertExistsById(studySessionId);
    return repository.findAllByStudySessionId(studySessionId);
  }

  private void validate(FlashcardRequest request) {
    assertNotNull(request);
    studySessionService.assertExistsById(request.getStudySessionId());
  }

  private void validate(Flashcard flashcard) {
    assertNotNull(flashcard);
    studySessionService.assertExistsById(flashcard.getStudySessionId());
  }

  private Flashcard flashcardFrom(FlashcardRequest request) {
    return mapper.flashcardFrom(request);
  }

}
```

### ✅ Strengths

- **Constructor-based injection** — ✔️ clean, testable
- **Extends `ValidatingService`** — ✔️ reusable, DRY validation
- **Custom exception (`NotFoundException`) usage** — ✔️ semantic error signaling
- **Clean separation of DTO → entity** — ✔️ delegated to mapper
- **Transactional scope** — ✔️ ensures atomicity in multi-repo flows
- **Defensive logic via `studySessionService.assertExistsById()`** — ✔️ pre-checks across layers
- **Immutable flow** — ✔️ avoids side effects; service remains stateless

---

# 🚨 Error-Handling Architecture Overview

The error-handling system in the Flashcards API centralizes exception processing, response formatting, and validation error transformation. It ensures consistent REST responses for both custom exceptions and validation failures.

All error-handling components reside in:

```
com.ken.flashcards.error
```

---

### 🧩 Key Components

| Component                     | Role                                                                 |
|------------------------------|----------------------------------------------------------------------|
| `GlobalExceptionHandler`     | Intercepts uncaught exceptions and maps them to REST responses       |
| `ResponseHandler`            | Interface providing helper methods for building standardized responses |
| `ValidationErrorExtractor`   | Transforms `MethodArgumentNotValidException` into readable error lists |
| `ErrorResponse`              | Structured payload representing simple error messages                 |

---

### 🔧 Error Response Flow

#### 1. **Service Layer Throws Exceptions**
- `NotFoundException`, `ConflictException`, `BadRequestException`
- Thrown when resources are missing, duplicated, or invalid

#### 2. **GlobalExceptionHandler Catches Them**
- Annotated with `@ControllerAdvice` and `@ExceptionHandler`
- Converts exceptions into `ErrorResponse` or validation maps

#### 3. **ValidationErrorExtractor Handles DTO Validation Errors**
- Used in `GlobalExceptionHandler`
- Extracts field-level messages from `MethodArgumentNotValidException`

#### 4. **ResponseHandler Builds ResponseEntities**
- Used when creating HTTP 201 responses or handling other status codes
- Utility interface shared across controller or advice layers

---

### 📘 Sample Output Structures

- **Custom Exception Response**
```json
{
  "error": "Cannot find flashcard with id = xyz"
}
```

- **Validation Error Response**
```json
{
  "errors": [
    "Session name must not be blank",
    "Category ID is required"
  ]
}
```

---

## 🧠 Design Notes

- 🧩 **Separation of concerns**: Each class handles a specific aspect of error flow
- 📤 **REST compliance**: Returns appropriate HTTP status codes (e.g., 400, 404, 409)
- 🛡️ **Safe and predictable**: Ensures all errors produce consistent, consumable output
- 🧪 **Testable**: Each layer (e.g., extractor, handler) can be independently unit-tested

## 🧪 Validation Error Extraction Overview

To handle client-side validation failures (e.g. form submission with missing or malformed fields), the application includes a specialized class for extracting readable error messages from exceptions triggered during binding.

This utility resides in:

```
com.ken.flashcards.error.ValidationErrorExtractor
```

It is used by the `GlobalExceptionHandler` to intercept `MethodArgumentNotValidException` and generate structured output for response payloads.

---

### 📘 Class Purpose

- Converts `FieldError` entries into user-friendly messages
- Aggregates validation issues into a simple `Map<String, List<String>>` structure
- Enables consistent formatting for 400 responses tied to invalid `@Valid` DTOs

---

### 🔧 Usage Example

In `GlobalExceptionHandler`:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
@ResponseStatus(BAD_REQUEST)
@ResponseBody
Map<String, List<String>> handle(MethodArgumentNotValidException exception) {
  var errorsExtractor = new ValidationErrorExtractor();
  return errorsExtractor.extractErrorsFrom(exception);
}
```

Produces output like:

```json
{
  "errors": [
    "Category name must not be blank",
    "Session ID is required"
  ]
}
```

---

## 🧠 Design Notes

- 📤 **REST-friendly output**: Returns a format easily consumable by frontend clients
- 🧩 **Separation of concerns**: Keeps formatting logic decoupled from error handler wiring
- 🛠️ **Spring-specific targeting**: Focuses specifically on `MethodArgumentNotValidException`, making it safe and predictable
- 🧪 **Evolvable formatting**: Could be extended to include field names or error codes if desired

---

# 📦 Controller Layer Overview

The controller layer serves as the external interface of the API, exposing REST endpoints that delegate business logic to service classes. Each controller is tightly scoped to a domain — categories, study sessions, and flashcards — and communicates directly with clients via HTTP.

---

## 🧩 Role of Controllers

- Accept JSON payloads and query parameters from HTTP requests
- Delegate logic to injected service interfaces
- Return standardized `ResponseEntity` objects via `ResponseHandler`
- Handle RESTful actions (CRUD, filtering) using clear, semantic routes
- Rely on `GlobalExceptionHandler` for error management (no try-catch clutter)

---

## 🗂 Controller Classes

| Controller Class           | Path Prefix        | Key Endpoints                                |
|----------------------------|--------------------|----------------------------------------------|
| `CategoryController`       | `/categories`      | `GET`, `POST`, `DELETE`, `GET /{id}`         |
| `StudySessionController`   | `/sessions`        | `GET`, `POST`, `DELETE`, `GET /category/{id}`|
| `FlashcardController`      | `/flashcards`      | `GET`, `POST`, `DELETE`, `GET /session/{id}` |

All classes implement the shared `ResponseHandler` interface to return clean and consistent responses.

---

## 🔧 Design Highlights

- 📦 **Constructor-based injection**: Promotes testability and clean dependency management
- 📤 **Thin controller logic**: All business rules are handled in service layer
- 📜 **RESTful conventions**: URL structure and HTTP methods follow standard REST design
- 📘 **DTO usage**: Input models (`CategoryRequest`, `StudySessionRequest`, `FlashcardRequest`) are decoupled from domain entities

---

## 🛡️ Error Handling Integration

- All controller classes remain agnostic to exception handling
- Exceptions thrown (e.g. `NotFoundException`, `ConflictException`) are intercepted by `GlobalExceptionHandler`
- Validation errors (from `@RequestBody`) are handled via `ValidationErrorExtractor`

---

# 🧭 Architectural Decision: `CategoryController` Design and Documentation

### 📌 Context
The `CategoryController` exposes a RESTful interface for managing `Category` entities. Early iterations focused on basic CRUD operations with minimal decoration. However, the controller has undergone systematic refinements to improve code clarity, response consistency, and documentation for external consumers.

### 🪜 Design Evolution

#### ✅ 1. Response Handling Simplification
Original implementations used explicit `ResponseEntity` constructors:
```java
return new ResponseEntity<>(data, HttpStatus.OK);
```
Refactored to the static `ok()` method for cleaner expression:
```java
return ResponseEntity.ok(data);
```
This pattern improves readability and aligns with idiomatic Spring usage.

#### ✅ 2. Interface Generalization
Switched from:
```java
Collection<Category>
```
to:
```java
Iterable<Category>
```
This change broadens compatibility with reactive and lazy data sources while preserving semantic clarity.

#### ✅ 3. Logic Extraction
Redundant service interactions within `update()` (e.g., `save()`, `existsById()`) were extracted into private utilities. This reduces cognitive load, enforces single-responsibility, and improves testability.

#### ✅ 4. Enhanced OpenAPI/Swagger Integration
Each controller method now includes precise `@ApiResponse` annotations:
- `responseCode` and `description` added across methods.
- Content negotiation specified via `mediaType = "application/json"`.
- Response body schema declared using `@Schema(implementation = Category.class)` for single objects and `@ArraySchema(...)` for collections.

#### ✅ 5. Defensive Error Documentation
The `DELETE /{id}` endpoint previously documented only success (`204 No Content`). It now includes:
```java
@ApiResponse(
  responseCode = "404",
  description = "Category does not exist",
  content = @Content(
    mediaType = "application/json",
    schema = @Schema(implementation = ErrorResponse.class)
  )
)
```
This explicitly documents failure states, empowering consumers with accurate error expectations and schema contracts.

### 📐 Outcome
These incremental changes reflect a commitment to robust API architecture:
- Controller methods remain clean and focused.
- Response behavior is predictable and semantically aligned with HTTP status codes.
- OpenAPI metadata accurately reflects system behavior, enabling powerful generated docs and smooth client integrations.

### 🧠 Lessons Learned
- Even small ergonomic changes (`ok()` vs `new ResponseEntity`) compound over time for cleaner code.
- Swagger annotations aren’t just for decoration — they represent a public contract and should be treated with the same rigor as method logic.
- Capturing design rationale in ADR form helps preserve decision lineage and serves as a reference for future team members.

# ⚙️ Application Configuration Overview

The `application.yml` file defines global settings for the Flashcards API, centralizing framework behavior, servlet routing, logging verbosity, and documentation paths. It supports modular development, secure deployment, and profile-aware testing.

---

## 🧩 Role of `application.yml`

- Centralizes configuration for **Spring Boot** application startup
- Establishes **RESTful base paths** using dynamic servlet mappings
- Configures **data source** parameters for local development
- Enables **SQL initialization behavior** for schema/data bootstrap
- Controls **logging verbosity** and target packages for debug visibility
- Defines **Swagger/OpenAPI** UI access paths for documentation

---

## 📂 Location in Project Structure

The file resides in:

```
src/main/resources/application.yml
```

This is the default Spring Boot config location recognized on application startup.

---

## 🛠 Configuration Highlights

| Section                 | Key Purpose                                           |
|-------------------------|------------------------------------------------------|
| `spring.datasource`     | JDBC connection URL, credentials (placeholder)       |
| `sql.init.mode`         | Auto-execute schema/data scripts on app startup      |
| `spring.servlet.path`   | Modular route nesting (`/api/v1/...`) for endpoints  |
| `server.port`           | Application entry port (`8080`)                      |
| `logging.level`         | Custom log output for Spring and app-specific logic  |
| `springdoc`             | Swagger API doc and UI path customization            |

Example dynamic routes resolved from nested config:
- `/api/v1/categories`
- `/api/v1/flashcards`
- `/api/v1/study-sessions`

---

## 🔒 Sensitive Fields

For public templates:
- `username` and `password` should remain **unset or commented**
- Real credentials can be externalized via environment variables or secrets managers

---

## 🧪 Dev/Test Profile Tip

To extend for multiple environments:
- Use `application-dev.yml` and `application-test.yml`
- Activate profiles with:  
  ```yaml
  spring:
    profiles:
      active: dev
  ```

This promotes safe switching between local, test, and production configurations.

---

## 🧠 Design Insight

The path templating strategy (`${spring.servlet.path.base}`) fosters scalable API modularity across domain controllers. This clean separation improves both endpoint discoverability and Swagger documentation clarity.

# 🗄️ SQL Schema Overview

The `schema.sql` file defines the relational data model for the Flashcards API, creating core tables and establishing integrity constraints across domain entities. It ensures that the database aligns with the structure of your domain model and supports cascading deletions for relational consistency.

---

## 📍 File Location

```
src/main/resources/schema.sql
```

Spring Boot executes this script automatically on startup when:

```yaml
spring.sql.init.mode: always
```

is active in the `application.yml`.

---

## 🧩 Role of `schema.sql`

- Initializes **category**, **study_session**, and **flashcard** tables
- Sets **primary keys** using `VARCHAR(40)` — compatible with UUID usage
- Applies **foreign key relationships** between tables
- Enforces **unique constraints** to maintain data integrity
- Implements **cascade operations** to auto-clean dependent records

---

## 🧱 Table Relationships

| Table          | Depends On       | Key Constraints                       |
|----------------|------------------|---------------------------------------|
| `category`     | —                | Unique `name`, primary key `id`       |
| `study_session`| `category`       | Foreign key `category_id` → `category.id` |
| `flashcard`    | `study_session`  | Foreign key `study_session_id` → `study_session.id` |

Foreign key constraints use:
- `ON DELETE CASCADE`
- `ON UPDATE CASCADE`  
to ensure referential integrity during parent table changes.

---

## ✅ Implementation

```sql
DROP TABLE IF EXISTS flashcard;
DROP TABLE IF EXISTS study_session;
DROP TABLE IF EXISTS category;

CREATE TABLE category (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    CONSTRAINT name_unique UNIQUE (name)
);

CREATE TABLE study_session (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    category_id VARCHAR(40) NOT NULL,
    name VARCHAR(30) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE flashcard (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    study_session_id VARCHAR(40) NOT NULL,
    question VARCHAR(200) NOT NULL,
    answer VARCHAR(300) NOT NULL,
    FOREIGN KEY (study_session_id) REFERENCES study_session(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
```

## 💡 Design Insights

- `DROP TABLE IF EXISTS` allows repeatable development cycles and local resets
- Explicit creation order respects dependencies:  
  `category` → `study_session` → `flashcard`
- Table naming aligns with controller paths and DTO structure

---

# 🌱 Seed Data Overview

Seed data is a predefined set of values used to populate the database during initial setup. It helps developers and testers work with meaningful content immediately—without needing to manually create records. In this context, it provides:
- Categories to organize learning material
- Study sessions grouped by topic
- Flashcards containing relevant questions and answers

This data mirrors real-world usage patterns and supports end-to-end API validation, UI rendering, and functional testing.

## 📍 File Location

```
src/main/resources/data.sql
```

## 🧩 Role of `data.sql`

This file initializes the database with curated sample data to support development, testing, and demonstration of key API features. It populates:
- `category` tables with topical subjects
- `study_session` entries that organize flashcards contextually
- `flashcard` records containing Q&A pairs for study practice

Designed for idempotent loading during application startup.

## ✅ Implementation

```sql
-- Categories
INSERT INTO category(id, name) VALUES('1', 'Art History');
INSERT INTO category(id, name) VALUES('2', 'Thermodynamics');
INSERT INTO category(id, name) VALUES('3', 'Computer Science');
INSERT INTO category(id, name) VALUES('4', 'American History');

-- Study Sessions
INSERT INTO study_session(id, category_id, name) VALUES('1', '1', 'Northern Renaissance');
INSERT INTO study_session(id, category_id, name) VALUES('2', '1', 'Renaissance');
INSERT INTO study_session(id, category_id, name) VALUES('3', '2', 'Second Law of Thermodynamics');
INSERT INTO study_session(id, category_id, name) VALUES('4', '3', 'Object Oriented Programming (OOP)');
INSERT INTO study_session(id, category_id, name) VALUES('5', '4', 'Presidents');

-- Flashcards
INSERT INTO flashcard(id, study_session_id, question, answer)
VALUES('1', '1', 'Who painted "The Garden of Earthly Delights"?', 'Hieronymus Bosch');
INSERT INTO flashcard(id, study_session_id, question, answer)
VALUES('2', '2', 'Who painted "The Last Supper"?', 'Leonardo da Vinci');
INSERT INTO flashcard(id, study_session_id, question, answer)
VALUES('3', '2', 'Who sculpted "David"?', 'Michelangelo');
INSERT INTO flashcard(id, study_session_id, question, answer)
VALUES('4', '3', 'What is a measure of disorder or randomness in a system?', 'Entropy');
INSERT INTO flashcard(id, study_session_id, question, answer)
VALUES('5', '4', 'What are three Object-Oriented Design Goals?', 'Adaptability, Reusability, Robustness');
INSERT INTO flashcard(id, study_session_id, question, answer)
VALUES('6', '4', 'What are three Object-Oriented Design Principles?', 'Abstraction, Encapsulation, Modularity');
INSERT INTO flashcard(id, study_session_id, question, answer)
VALUES('7', '5', 'Who issued the Emancipation Proclamation?', 'Abraham Lincoln');
```

## 💡 Design Insights

- **Consistent ID conventions** eliminate key collisions across entities
- **Modular structure** supports testability and easy expansion of study topics
- **Cascading relationships** align with schema enforcement, enabling referential integrity during deletions/updates

---

# 🧪 Testing Overview

### Context
Following the completion of core layers (Model, DTO, Mapper, Service, Controller, Global Exception Handling), the next phase involves establishing a reliable testing strategy to validate business logic (Service), input/output mapping (Controller & Mapper), and persistence behavior (Repository).

### Decision
We will implement both **unit tests** and **integration tests** using `JUnit 5`, `Mockito`, and Spring-specific annotations. This phase strengthens confidence in service logic, controller request handling, and data access behavior.

### Application Structure

Create the following mirrored test package structure under `src/test/java/com/ken/flashcards/`:

```
src/
├── test/
│   └── java/
│       └── com/
│           └── ken/
│               └── flashcards/
│                   ├── controller        ← Integration & endpoint tests
│                   ├── service           ← Unit tests with mocked repos
│                   ├── repository        ← Data access layer tests (optional)
│                   └── mapper            ← Logic validation tests (optional)
```

### Test Strategy

| Layer      | Test Scope                     | Key Annotations                         | Purpose                                              |
|------------|--------------------------------|-----------------------------------------|------------------------------------------------------|
| Controller | Integration / API surface      | `@WebMvcTest`, `@SpringBootTest`        | Route mapping, request/response, exception flow     |
| Service    | Unit (mocked dependencies)     | `@ExtendWith(MockitoExtension.class)`   | Business logic, method delegation, edge cases       |
| Repository | Data access layer              | `@DataJpaTest`                          | DB query validation, transactional behavior         |
| Mapper     | Lightweight logic validation   | Plain JUnit                             | DTO ↔ entity conversion accuracy                    |

### Goals

- ✅ Ensure service methods correctly delegate, transform, and handle exceptions
- ✅ Validate controller endpoints for expected inputs, outputs, and error states
- ✅ Verify repository queries interact predictably with real or in-memory DB layers
- ✅ Test mappers for accurate transformation between DTOs and domain models

# 🧪 Service Layer: Initial Test Implementation

## 🛎️ CategoryServiceImpl

#### Setup
We begin by validating the `CategoryServiceImpl`, the concrete implementation of business logic for category-related operations. This testing effort focuses on ensuring correct delegation to the repository and expected data handling behaviors.

Create the test class under the service package:

```
src/test/java/com/ken/flashcards/service/CategoryServiceImplTest.java
```

This class uses `JUnit 5` and `Mockito` annotations:

- `@ExtendWith(MockitoExtension.class)` to enable Mockito in JUnit
- `@Mock` for mocking dependencies like `CategoryRepository` and `CategoryMapper`
- `@InjectMocks` to inject the service implementation into the test context

#### First Unit Test: `findAll()`

The first method we test is `findAll()`, which retrieves all categories sorted by name. This is a lightweight method, ideal for kicking off service layer validation.

```java
@Test
void findsAllOrderedByName() {
  when(categoryRepository.findAllByOrderByNameAsc()).thenReturn(categories);
  assertEquals(categories, categoryService.findAll());
  verify(categoryRepository, times(1)).findAllByOrderByNameAsc();
}
```

This test verifies:

- Correct method delegation to `categoryRepository`
- Sorted data retrieval via `findAllByOrderByNameAsc()`
- Consistency between mock output and service return value

---

# 🧪 Mapper Test Suite Overview

Mapper tests ensure that DTO-to-entity transformations behave predictably, with correct field mapping and reliable ID generation. These tests complement validation and controller-layer logic by verifying the integrity of your domain construction pipeline.

Each test class directly targets a mapper implementation, mocking `IdGenerator` where applicable to decouple randomness and guarantee deterministic outputs.

### 📦 Package Location
All mapper tests are located in:
```
com.ken.flashcards.mapper
```

---

### 📌 Test Coverage Summary

| Test Class                      | Purpose                                                   |
|---------------------------------|-----------------------------------------------------------|
| `CategoryMapperImplTest`        | Verifies mapping of `CategoryRequest` → `Category`         |
| `FlashcardMapperImplTest`       | Verifies mapping of `FlashcardRequest` → `Flashcard`       |
| `StudySessionMapperImplTest`    | Verifies mapping of `StudySessionRequest` → `StudySession` |

All tests use isolated unit testing strategies with direct assertions on mapped field values.

---

### ⚙️ Example: `FlashcardMapperImplTest`

```java
class FlashcardMapperImplTest {

  private IdGenerator idGenerator;
  private FlashcardMapper flashcardMapper;

  @BeforeEach
  void setUp() {
    idGenerator = mock(IdGenerator.class);
    flashcardMapper = new FlashcardMapperImpl(idGenerator);
  }

  @Test
  void shouldMapFlashcardRequestToEntity() {
    when(idGenerator.generateId()).thenReturn("flashcard-123");

    FlashcardRequest request = new FlashcardRequest("session-01", "What is encapsulation?", "Wrapping data and methods together");
    Flashcard result = flashcardMapper.flashcardFrom(request);

    assertEquals("flashcard-123", result.id());
    assertEquals("session-01", result.studySessionId());
    assertEquals("What is encapsulation?", result.question());
    assertEquals("Wrapping data and methods together", result.answer());
  }
}
```

---

## 🧠 Test Design Notes

- 🧪 **Isolated behavior**: Each test validates only mapper logic — external collaborators are mocked.
- 🧾 **Field-level assertions**: Tests verify that all properties are transferred correctly from DTO to entity.
- 🔧 **Mocked `IdGenerator`**: Ensures ID values are predictable and test-safe.
- 🧼 **No side effects**: Mappers are pure functions, ideal for clean unit testing.

---

# 🧪 DTO Validation Test Overview

The app’s request DTOs include field-level validation to ensure data integrity before entering the service layer. Dedicated unit tests confirm that each constraint behaves correctly under common failure conditions, safeguarding against malformed input and reinforcing the API's contract.

Validation tests focus on constraint annotations like `@NotBlank`, using the Bean Validation API (JSR-380) to simulate how payloads are enforced during runtime.

### 📦 Package Location
All DTO tests are located in:
```
com.ken.flashcards.dto
```

---

### 📌 Test Coverage Summary

| Test Class                | Purpose                                                                 |
|--------------------------|-------------------------------------------------------------------------|
| `CategoryRequestTest`     | Verifies `@NotBlank` constraint on `name` field                         |
| `StudySessionRequestTest` | Verifies `@NotBlank` constraints on `categoryId` and `name`             |
| `FlashcardRequestTest`    | Verifies `@NotBlank` constraints on `studySessionId`, `question`, `answer` |

Each test ensures blank inputs trigger correct constraint violations and that well-formed payloads pass successfully.

---

### ⚙️ Example: `StudySessionRequestTest`

```java
@Test
void shouldFailValidationWhenCategoryIdIsBlank() {
  StudySessionRequest request = new StudySessionRequest("  ", "Art History");
  Set<ConstraintViolation<StudySessionRequest>> violations = validator.validate(request);

  assertFalse(violations.isEmpty());
  assertEquals("category id is required", violations.iterator().next().getMessage());
}
```

---

## 🧠 Test Design Notes

- 🔍 **JSR-380 Validator**: Each test uses Jakarta Bean Validation to detect violations, mimicking controller-level enforcement.
- 🧪 **Field-level coverage**: Tests focus on `@NotBlank` annotations with custom messages.
- 📎 **Custom failure messaging**: Each DTO uses domain-specific feedback, which tests verify directly.
- 🔁 **Stateless and isolated**: No Spring context required — tests rely purely on the validation engine.
