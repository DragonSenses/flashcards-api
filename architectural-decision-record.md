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

## ✉️ DTO Overview

The application uses data transfer objects (DTOs) to encapsulate input payloads for resource creation. These classes separate internal domain logic from exposed API contracts and include validation constraints for safer API consumption.

Each DTO is defined as an immutable class using Lombok, and they're located in:

```
com.ken.flashcards.dto
```

---

### 📘 1. `CategoryRequest.java`

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

### 📘 2. `StudySessionRequest.java`

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

### 📘 3. `FlashcardRequest.java`

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
