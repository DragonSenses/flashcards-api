# Architectural Decision Record

# flashcards-api

A modular and well-documented RESTful backend for managing flashcard-based study sessions. Built with Java, Spring Boot, Swagger UI.

## Development

Model View Controller. This comprehensive document aims to guide the steps of development and ease onboarding. 

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