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

import static com.ken.flashcards.constants.ExceptionMessages.CANNOT_FIND_FLASHCARD_BY_ID;
import static com.ken.flashcards.constants.ExceptionMessages.CANNOT_FIND_STUDY_SESSION_BY_ID;

@Sql({"/data.sql"})
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FlashcardIntegrationTest {

  @Value("${spring.servlet.path.flashcards}")
  private String path;

  @Autowired
  WebTestClient client;

  @Test
  void loadTestData() {
    client.get().uri(path).accept(APPLICATION_JSON).exchange().expectStatus().isOk().expectBody()
        .jsonPath("$").isArray()
        .jsonPath("$[?(@.question == 'What is a measure of disorder or randomness in a system?')]")
        .exists();
  }

  @Test
  void loadSeededFlashcards() {
    client.get().uri(path).accept(APPLICATION_JSON).exchange().expectStatus().isOk().expectBody()
        .jsonPath("$").isArray()
        .jsonPath("$[?(@.question == 'What is the periodic law in chemistry?')]").exists()
        .jsonPath("$[?(@.question == 'Who developed the first successful polio vaccine?')]")
        .exists().jsonPath("$[?(@.question == 'What is the Pythagorean theorem used for?')]")
        .exists().jsonPath("$[?(@.question == 'What caused the fall of the Roman Empire?')]")
        .exists()
        .jsonPath("$[?(@.question == 'What is the difference between mitosis and meiosis?')]")
        .exists().jsonPath("$[?(@.question == 'Who painted the ceiling of the Sistine Chapel?')]")
        .exists().jsonPath("$[?(@.question == 'What is the function of red blood cells?')]")
        .exists().jsonPath("$[?(@.question == 'What is the derivative of sin(x)?')]").exists()
        .jsonPath("$[?(@.question == 'What event triggered World War I?')]").exists()
        .jsonPath("$[?(@.question == 'What is the role of enzymes in digestion?')]").exists()
        .jsonPath("$[?(@.question == 'Who discovered the law of gravity?')]").exists()
        .jsonPath("$[?(@.question == 'What is the golden ratio in art and architecture?')]")
        .exists().jsonPath("$[?(@.question == 'What is the boiling point of water at sea level?')]")
        .exists().jsonPath("$[?(@.question == 'What is the main function of the mitochondria?')]")
        .exists().jsonPath("$[?(@.question == 'What is the significance of the Magna Carta?')]")
        .exists().jsonPath("$[?(@.question == 'What is the S of the SOLID principles?')]").exists()
        .jsonPath("$[?(@.question == 'What are the four pillars of OOP?')]").exists()
        .jsonPath("$[?(@.question == 'What is a higher order function?')]").exists()
        .jsonPath("$[?(@.question == 'What is function composition?')]").exists()
        .jsonPath("$[?(@.question == 'What is the formula for calculating kinetic energy?')]")
        .exists()
        .jsonPath(
            "$[?(@.question == 'What is the difference between a conductor and an insulator?')]")
        .exists().jsonPath("$[?(@.question == 'Who composed the ninth symphony?')]").exists()
        .jsonPath("$[?(@.question == 'Who composed the Jupiter symphony?')]").exists()
        .jsonPath("$[?(@.question == 'What is the tonic?')]").exists()
        .jsonPath("$[?(@.question == 'What is a chord progression?')]").exists()
        .jsonPath("$[?(@.question == 'What is the definition of a prime number?')]").exists()
        .jsonPath("$[?(@.question == 'Who was the first emperor of China?')]").exists()
        .jsonPath("$[?(@.question == 'What is the role of chlorophyll in photosynthesis?')]")
        .exists().jsonPath("$[?(@.question == 'What are the types of chemical reactions?')]")
        .exists().jsonPath("$[?(@.question == 'What is a covalent bond?')]").exists()
        .jsonPath("$[?(@.question == 'What are the four types of nuclear reactions?')]").exists()
        .jsonPath("$[?(@.question == 'What is nuclear fusion?')]").exists();
  }

  @DisplayName("GET /flashcards/{id} should return flashcard when ID exists")
  @Test
  void shouldReturnFlashcardByIdWhenExists() {
    String flashcardId = "1";
    String expectedResponseBody = """
        {
            "id":"1",
            "studySessionId":"1",
            "question":"What kind of star is the sun?",
            "answer":"Yellow dwarf"
        }
        """;

    client.get().uri(path + "/" + flashcardId).accept(APPLICATION_JSON).exchange().expectStatus()
        .isOk().expectBody().json(expectedResponseBody);
  }

  @DisplayName("GET /flashcards/{id} should return 404 when flashcard ID does not exist")
  @Test
  void shouldReturnNotFoundWhenFlashcardIdDoesNotExist() {
    String nonexistentId = "3";
    String errorMessage = format(CANNOT_FIND_FLASHCARD_BY_ID, nonexistentId);

    client.get().uri(path + "/" + nonexistentId).accept(APPLICATION_JSON).exchange().expectStatus()
        .isNotFound().expectBody().json("{\"error\":\"" + errorMessage + "\"}");
  }

  @Test
  void findsAllByStudySessionId() {
    client.get().uri(path + "/details?studySessionId=1").accept(APPLICATION_JSON).exchange()
        .expectStatus().isOk().expectBody().jsonPath("$").isArray().json("""
            [
                {
                    'id':'1',
                    'studySessionId':'1',
                    'question':'What kind of star is the sun?',
                    'answer':'Yellow dwarf'
                }
            ]
            """);
  }

  @DisplayName("GET /flashcards/details should return 404 when studySessionId does not exist")
  @Test
  void shouldReturnNotFoundWhenStudySessionIdDoesNotExist() {
    String nonexistentStudySessionId = "3";
    String errorMessage = format(CANNOT_FIND_STUDY_SESSION_BY_ID, nonexistentStudySessionId);

    client.get().uri(path + "/details?studySessionId=" + nonexistentStudySessionId)
        .accept(APPLICATION_JSON).exchange().expectStatus().isNotFound().expectBody()
        .json("{\"error\":\"" + errorMessage + "\"}");
  }

  @DisplayName("POST /flashcards should create flashcard when request is valid")
  @Test
  void shouldCreateFlashcardWhenRequestIsValid() {
    String requestBody = """
        {
            "studySessionId":"1",
            "question":"Which is the largest planet of the solar system?",
            "answer":"Jupiter"
        }
        """;

    client.post().uri(path).contentType(APPLICATION_JSON).bodyValue(requestBody).exchange()
        .expectStatus().isCreated().expectBody().jsonPath("$.id").exists()
        .jsonPath("$.studySessionId").isEqualTo("1").jsonPath("$.question")
        .isEqualTo("Which is the largest planet of the solar system?").jsonPath("$.answer")
        .isEqualTo("Jupiter");
  }

  @DisplayName("POST /flashcards should return 404 when studySessionId does not exist")
  @Test
  void shouldReturnNotFoundWhenCreatingFlashcardWithNonexistentStudySession() {
    String invalidStudySessionId = "3";
    String requestBody = """
        {
            "studySessionId":"3",
            "question":"What is tennis?",
            "answer":"A sport that is played with a ball and two rackets"
        }
        """;
    String errorMessage = format(CANNOT_FIND_STUDY_SESSION_BY_ID, invalidStudySessionId);

    client.post().uri(path).contentType(APPLICATION_JSON).bodyValue(requestBody).exchange()
        .expectStatus().isNotFound().expectBody().json("{\"error\":\"" + errorMessage + "\"}");
  }

  @DisplayName("POST /flashcards should return 400 when request body is empty")
  @Test
  void shouldReturnBadRequestWhenCreatingFlashcardWithEmptyRequestBody() {
    client.post().uri(path).contentType(APPLICATION_JSON).bodyValue("").exchange().expectStatus()
        .isBadRequest();
  }

  @DisplayName("POST /flashcards should return 400 with validation errors when fields are empty")
  @Test
  void shouldReturnBadRequestWithErrorsWhenCreatingFlashcardWithEmptyValues() {
    String requestBody = """
        {
            "studySessionId":"",
            "question":"",
            "answer":""
        }
        """;

    client.post().uri(path).contentType(APPLICATION_JSON).bodyValue(requestBody).exchange()
        .expectStatus().isBadRequest().expectBody().json("""
            {
                "errors": [
                    "study session id is required",
                    "question is required",
                    "answer is required"
                ]
            }
            """);
  }

  @DisplayName("PUT /flashcards should update flashcard when ID exists")
  @Test
  void shouldUpdateFlashcardWhenIdExists() {
    String requestBody = """
        {
            "id":"1",
            "studySessionId":"1",
            "question":"Which is the closest planet to the Sun?",
            "answer":"Mercury"
        }
        """;

    client.put().uri(path).contentType(APPLICATION_JSON).bodyValue(requestBody).exchange()
        .expectStatus().isOk().expectBody().json(requestBody);
  }

  @DisplayName("PUT /flashcards should create flashcard when ID does not exist")
  @Test
  void shouldCreateFlashcardWhenIdDoesNotExist() {
    String requestBody = """
        {
            "id":"3",
            "studySessionId":"1",
            "question":"Which is the closest planet to the Sun?",
            "answer":"Mercury"
        }
        """;

    client.put().uri(path).contentType(APPLICATION_JSON).bodyValue(requestBody).exchange()
        .expectStatus().isCreated().expectBody().json(requestBody);
  }

  @DisplayName("DELETE /flashcards/{id} should remove flashcard when ID exists")
  @Test
  void shouldDeleteFlashcardByIdWhenExists() {
    String flashcardId = "1";

    client.delete().uri(path + "/" + flashcardId).accept(APPLICATION_JSON).exchange().expectStatus()
        .isNoContent();
  }

  @DisplayName("DELETE /flashcards/{id} should return 404 when ID does not exist")
  @Test
  void shouldReturnNotFoundWhenDeletingFlashcardWithNonexistentId() {
    String nonexistentId = "3";
    String errorMessage = format(CANNOT_FIND_FLASHCARD_BY_ID, nonexistentId);

    client.delete().uri(path + "/" + nonexistentId).accept(APPLICATION_JSON).exchange()
        .expectStatus().isNotFound().expectBody().json("{\"error\":\"" + errorMessage + "\"}");
  }
}
