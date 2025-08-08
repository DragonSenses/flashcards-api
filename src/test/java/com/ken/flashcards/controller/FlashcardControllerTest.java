package com.ken.flashcards.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ken.flashcards.model.Flashcard;
import com.ken.flashcards.service.FlashcardService;

@WebMvcTest(FlashcardController.class)
public class FlashcardControllerTest extends ControllerTestBase {

  @Value("${spring.servlet.path.flashcards}")
  private String flashcardsPath;

  @MockitoBean
  FlashcardService flashcardService;

  @Autowired
  MockMvc mockMvc;

  private Flashcard flashcard;

  private final String expectedFlashcardId = "flashcard-id-001";
  private final String expectedStudySessionId = "science-thermodynamics-002";
  private final String expectedQuestion =
      "What is a measure of disorder or randomness in a system?";
  private final String expectedAnswer = "Entropy";

  @BeforeEach
  void setup() {
    this.flashcard = new Flashcard(expectedFlashcardId, expectedStudySessionId, expectedQuestion,
        expectedAnswer);
  }
}
