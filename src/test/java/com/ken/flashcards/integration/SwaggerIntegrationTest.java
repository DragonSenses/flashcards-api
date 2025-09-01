package com.ken.flashcards.integration;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.http.MediaType.TEXT_HTML;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SwaggerIntegrationTest {

  @Autowired
  private WebTestClient client;

  @Test
  void swaggerUserInterfaceIsLoadedCorrectly() throws Exception {
    client.get().uri("/swagger-ui/index.html").accept(TEXT_HTML).exchange().expectStatus().isOk()
        .expectBody(String.class).value(containsString("<title>Swagger UI</title>"));
  }

}
