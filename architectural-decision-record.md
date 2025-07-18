# Architectural Decision Record

# flashcards-api

A modular and well-documented RESTful backend for managing flashcard-based study sessions. Built with Java, Spring Boot, Swagger UI.

## Development

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
