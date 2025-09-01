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

  
}
