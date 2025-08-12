package com.ken.flashcards.integration;

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

@Sql({"/data.sql"})
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CategoryIntegrationTest {

  @Value("${spring.servlet.path.categories}")
  private String path;

  @Autowired
  private WebTestClient client;

  @DisplayName("GET /categories returns seeded category 'Computer Science'")
  @Test
  void returnsSeededCategoryComputerScience() {
    client.get().uri(path).accept(APPLICATION_JSON).exchange().expectStatus().isOk().expectBody()
        .jsonPath("$").isArray().jsonPath("$[?(@.name == 'Computer Science')]").exists();
  }

}
