# flashcards-api
A modular and well-documented RESTful backend for managing flashcard-based study sessions. Built with Java, Spring Boot, Swagger UI.

# High Level Architecture

**Layers**
- Client / Swagger UI
- Controller Layer
- Service Layer
- Mapper & DTO
- Repository Layer
- MySQL Database

Entities
[ Category ]
     ↓ categoryId
[ StudySession ]
     ↓ studySessionId
[ Flashcard ]

```
[ Service Layer ]
      ↓ throws
[ NotFoundException / ConflictException / BadRequestException ]
      ↓ captured by
[ ApiExceptionHandler (@ControllerAdvice) ]
      ↓ mapped to
[ ErrorResponse ]
      ↓ returned as
[ HTTP Response to Client ]
```

Error Handling
```
[ MethodArgumentNotValidException ]
      ↓ uses
[ MethodArgumentNotValidExceptionMapper ]
      ↓ returns
[ Map<String, List<String>> ]
```

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