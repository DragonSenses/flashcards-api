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

  @DisplayName("GET /categories returns all seeded categories")
  @Test
  void returnsAllSeededCategories() {
    client.get().uri(path).accept(APPLICATION_JSON).exchange().expectStatus().isOk().expectBody()
        .jsonPath("$").isArray()
        .jsonPath("$[?(@.name == 'Applied Mathematics')]").exists()
        .jsonPath("$[?(@.name == 'Biology')]").exists()
        .jsonPath("$[?(@.name == 'Business')]").exists()
        .jsonPath("$[?(@.name == 'Calculus')]").exists()
        .jsonPath("$[?(@.name == 'Chemistry')]").exists()
        .jsonPath("$[?(@.name == 'Discrete Math')]").exists()
        .jsonPath("$[?(@.name == 'Drama')]").exists()
        .jsonPath("$[?(@.name == 'Engineering')]").exists()
        .jsonPath("$[?(@.name == 'History')]").exists()
        .jsonPath("$[?(@.name == 'Nursing')]").exists()
        .jsonPath("$[?(@.name == 'Physics')]").exists()
        .jsonPath("$[?(@.name == 'Psychology')]").exists()
        .jsonPath("$[?(@.name == 'Web Design')]").exists()
        .jsonPath("$[?(@.name == 'Zoology')]").exists();
  }

  @DisplayName("GET /categories returns categories sorted alphabetically")
  @Test
  void returnsCategoriesSortedAlphabetically() {
    client.get().uri(path).accept(APPLICATION_JSON).exchange().expectStatus().isOk().expectBody()
        .jsonPath("$.length()").isEqualTo(14).jsonPath("$[0].name").isEqualTo("Applied Mathematics")
        .jsonPath("$[14].name").isEqualTo("Zoology");
  }

  @DisplayName("GET /categories/{id} returns category when ID exists")
  @Test
  void returnsCategoryByIdWhenExists() {
    client.get().uri(path + "/2").accept(APPLICATION_JSON).exchange().expectStatus().isOk()
        .expectBody().json("{'id':'2', 'name':'Thermodynamics'}");
  }

}
