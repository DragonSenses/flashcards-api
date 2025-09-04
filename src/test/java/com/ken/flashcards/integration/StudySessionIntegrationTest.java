package com.ken.flashcards.integration;

import static java.lang.String.format;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.ken.flashcards.constants.ExceptionMessages.CANNOT_FIND_CATEGORY_BY_ID;
import static com.ken.flashcards.constants.ExceptionMessages.CANNOT_FIND_STUDY_SESSION_BY_ID;

@Sql({"/data.sql"})
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StudySessionIntegrationTest {

  @Value("${spring.servlet.path.study-sessions}")
  private String path;

  @Autowired
  WebTestClient client;

  private static final String NONEXISTENT_CATEGORY_ID = "987654321";
  private static final String NONEXISTENT_SESSION_ID = "987654321";

  @Test
  void loadTestData() {
    client.get().uri(path).accept(APPLICATION_JSON).exchange().expectStatus().isOk().expectBody()
        .jsonPath("$").isArray().jsonPath("$[?(@.name == 'Art History')]").exists();
  }

  @DisplayName("GET /study-sessions should return all seeded study sessions")
  @Test
  void shouldReturnAllSeededStudySessions() {
    client.get().uri(path).accept(APPLICATION_JSON).exchange().expectStatus().isOk().expectBody()
        .jsonPath("$").isArray().jsonPath("$[?(@.name == 'Chemistry fundamentals')]").exists()
        .jsonPath("$[?(@.name == 'Medical breakthroughs')]").exists()
        .jsonPath("$[?(@.name == 'Mathematical principles')]").exists()
        .jsonPath("$[?(@.name == 'Ancient civilizations')]").exists()
        .jsonPath("$[?(@.name == 'Cell biology')]").exists()
        .jsonPath("$[?(@.name == 'Renaissance art')]").exists()
        .jsonPath("$[?(@.name == 'Human anatomy')]").exists()
        .jsonPath("$[?(@.name == 'Calculus basics')]").exists()
        .jsonPath("$[?(@.name == 'World history')]").exists()
        .jsonPath("$[?(@.name == 'Digestive system')]").exists()
        .jsonPath("$[?(@.name == 'Physics discoveries')]").exists()
        .jsonPath("$[?(@.name == 'Design and proportion')]").exists()
        .jsonPath("$[?(@.name == 'Physical properties of water')]").exists()
        .jsonPath("$[?(@.name == 'Cellular respiration')]").exists()
        .jsonPath("$[?(@.name == 'Legal history')]").exists()
        .jsonPath("$[?(@.name == 'Software design principles')]").exists()
        .jsonPath("$[?(@.name == 'Object oriented programming')]").exists()
        .jsonPath("$[?(@.name == 'Functional programming')]").exists()
        .jsonPath("$[?(@.name == 'Physics formulas')]").exists()
        .jsonPath("$[?(@.name == 'Electrical properties')]").exists()
        .jsonPath("$[?(@.name == 'Classical composers')]").exists()
        .jsonPath("$[?(@.name == 'Music theory')]").exists()
        .jsonPath("$[?(@.name == 'Mathematics')]").exists()
        .jsonPath("$[?(@.name == 'Chinese history')]").exists()
        .jsonPath("$[?(@.name == 'Photosynthesis')]").exists()
        .jsonPath("$[?(@.name == 'Chemical reactions')]").exists()
        .jsonPath("$[?(@.name == 'Bonding types')]").exists()
        .jsonPath("$[?(@.name == 'Nuclear chemistry')]").exists()
        .jsonPath("$[?(@.name == 'Fusion reactions')]").exists();
  }

  @DisplayName("GET /study-sessions/{id} should return study session when ID exists")
  @Test
  void shouldReturnStudySessionByIdWhenExists() {
    String studySessionId = "1";

    client.get().uri(path + "/" + studySessionId).accept(APPLICATION_JSON).exchange().expectStatus()
        .isOk().expectBody().json("""
            {
                "id":"1",
                "categoryId":"1",
                "name":"Art History"
            }
            """);
  }

  @DisplayName("GET /study-sessions/{id} should return 404 when ID does not exist")
  @Test
  void shouldReturnNotFoundWhenStudySessionIdDoesNotExist() {
    String errorMessage = format(CANNOT_FIND_STUDY_SESSION_BY_ID, NONEXISTENT_SESSION_ID);

    client.get().uri(path + "/" + NONEXISTENT_SESSION_ID).accept(APPLICATION_JSON).exchange().expectStatus()
        .isNotFound().expectBody().json("{\"error\":\"" + errorMessage + "\"}");
  }

  @DisplayName("GET /study-sessions/details should return all sessions for a given categoryId")
  @Test
  void shouldReturnStudySessionsByCategoryId() {
    String categoryId = "1";

    client.get().uri(path + "/details?categoryId=" + categoryId).accept(APPLICATION_JSON).exchange()
        .expectStatus().isOk().expectBody().jsonPath("$").isArray().json("""
            [
                {
                    "id":"1",
                    "categoryId":"1",
                    "name":"Art History"
                }
            ]
            """);
  }

  @DisplayName("GET /study-sessions/details should return 404 when categoryId does not exist")
  @Test
  void shouldReturnNotFoundWhenCategoryIdDoesNotExist() {
    String errorMessage = format(CANNOT_FIND_CATEGORY_BY_ID, NONEXISTENT_CATEGORY_ID);

    client.get().uri(path + "/details?categoryId=" + NONEXISTENT_CATEGORY_ID).accept(APPLICATION_JSON)
        .exchange().expectStatus().isNotFound().expectBody()
        .json("{\"error\":\"" + errorMessage + "\"}");
  }

  @DisplayName("POST /study-sessions should create study session when request is valid")
  @Test
  void shouldCreateStudySessionWhenRequestIsValid() {
    String categoryId = "1";
    String name = "Stellar Classification";

    client.post().uri(path).contentType(APPLICATION_JSON)
        .bodyValue("{\"categoryId\":\"" + categoryId + "\", \"name\":\"" + name + "\"}").exchange()
        .expectStatus().isCreated().expectBody().jsonPath("$.id").exists().jsonPath("$.categoryId")
        .isEqualTo(categoryId).jsonPath("$.name").isEqualTo(name);
  }

  @DisplayName("POST /study-sessions should return 404 when categoryId does not exist")
  @Test
  void shouldReturnNotFoundWhenCreatingStudySessionWithNonexistentCategory() {
    String errorMessage = format(CANNOT_FIND_CATEGORY_BY_ID, NONEXISTENT_CATEGORY_ID);

    client.post().uri(path).contentType(APPLICATION_JSON)
        .bodyValue("{\"categoryId\":\"" + NONEXISTENT_CATEGORY_ID
            + "\", \"name\":\"Classical Music authors\"}")
        .exchange().expectStatus().isNotFound().expectBody()
        .json("{\"error\":\"" + errorMessage + "\"}");
  }

  @DisplayName("POST /study-sessions should return 400 when request body is empty")
  @Test
  void shouldReturnBadRequestWhenCreatingStudySessionWithEmptyRequestBody() {
    client.post().uri(path).contentType(APPLICATION_JSON).bodyValue("").exchange().expectStatus()
        .isBadRequest();
  }

  @DisplayName("POST /study-sessions should return 400 with error when categoryId is empty")
  @Test
  void shouldReturnBadRequestWithErrorWhenCreatingStudySessionWithEmptyCategoryId() {
    client.post().uri(path).contentType(APPLICATION_JSON)
        .bodyValue("{\"categoryId\":\"\", \"name\":\"Esports\"}").exchange().expectStatus()
        .isBadRequest().expectBody().json("{\"errors\":[\"category id is required\"]}");
  }

  @DisplayName("POST /study-sessions should return 400 with error when name is empty")
  @Test
  void shouldReturnBadRequestWithErrorWhenCreatingStudySessionWithEmptyName() {
    client.post().uri(path).contentType(APPLICATION_JSON)
        .bodyValue("{\"categoryId\":\"321\", \"name\":\"\"}").exchange().expectStatus().isBadRequest()
        .expectBody().json("{\"errors\":[\"name is required\"]}");
  }

  @DisplayName("PUT /study-sessions should update study session when ID exists")
  @Test
  void shouldUpdateStudySessionWhenIdExists() {
    String requestBody = """
        {
            "id":"1",
            "categoryId":"1",
            "name":"Stellar Classification"
        }
        """;

    client.put().uri(path).contentType(APPLICATION_JSON).bodyValue(requestBody).exchange()
        .expectStatus().isOk().expectBody().json(requestBody);
  }

  @DisplayName("PUT /study-sessions should create study session when ID does not exist")
  @Test
  void shouldCreateStudySessionWhenIdDoesNotExist() {
    String requestBody = """
        {
            "id":"321",
            "categoryId":"1",
            "name":"Stellar Classification"
        }
        """;

    client.put().uri(path).contentType(APPLICATION_JSON).bodyValue(requestBody).exchange()
        .expectStatus().isCreated().expectBody().json(requestBody);
  }

  @DisplayName("PUT /study-sessions should return 404 when categoryId does not exist")
  @Test
  void shouldReturnNotFoundWhenUpdatingStudySessionWithNonexistentCategory() {
    String errorMessage = format(CANNOT_FIND_CATEGORY_BY_ID, NONEXISTENT_CATEGORY_ID);

    client.put().uri(path).contentType(APPLICATION_JSON)
        .bodyValue("{\"id\":\"1\", \"categoryId\":\"" + NONEXISTENT_CATEGORY_ID
            + "\", \"name\":\"Classical Music\"}")
        .exchange().expectStatus().isNotFound().expectBody()
        .json("{\"error\":\"" + errorMessage + "\"}");
  }

}
