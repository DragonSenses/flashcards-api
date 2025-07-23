# Architectural Decision Record

# flashcards-api

A modular and well-documented RESTful backend for managing flashcard-based study sessions. Built with Java, Spring Boot, Swagger UI.

## Development

Model View Controller. This comprehensive document aims to guide the steps of development and ease onboarding. 

### Application Structure

Create the following package structure under `src/main/java/com/ken/flashcards`:

```
├── controller
├── service
├── repository
├── model
├── mapper
├── dto
├── config
└── exception
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
