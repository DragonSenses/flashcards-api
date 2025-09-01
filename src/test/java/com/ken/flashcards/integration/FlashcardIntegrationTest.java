package com.ken.flashcards.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

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

}
