package com.ken.flashcards.controller;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ken.flashcards.constants.ExceptionMessages;
import com.ken.flashcards.exception.NotFoundException;
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

  @DisplayName("GET /flashcards - should return 200 with all flashcards")
  @Test
  void shouldReturn200WithAllFlashcards() throws Exception {
    when(flashcardService.findAll()).thenReturn(Set.of(flashcard));

    mockMvc.perform(get(flashcardsPath)).andExpect(status().isOk())
        .andExpect(content().json(serialize(Set.of(flashcard))));
  }

  @DisplayName("GET /flashcards/{id} - should return 200 when flashcard exists")
  @Test
  void shouldReturn200WhenFlashcardExistsById() throws Exception {
    when(flashcardService.findById(expectedFlashcardId)).thenReturn(flashcard);

    mockMvc.perform(get(flashcardsPath + "/" + expectedFlashcardId)).andExpect(status().isOk())
        .andExpect(content().json(serialize(flashcard)));
  }

  @DisplayName("GET /flashcards/{id} - should return 404 when flashcard is not found")
  @Test
  void shouldReturn404WhenFlashcardDoesNotExistById() throws Exception {
    String errorMessage =
        String.format(ExceptionMessages.CANNOT_FIND_FLASHCARD_BY_ID, expectedFlashcardId);

    when(flashcardService.findById(expectedFlashcardId))
        .thenThrow(new NotFoundException(errorMessage));

    mockMvc.perform(get(flashcardsPath + "/" + expectedFlashcardId))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
        .andExpect(content().json("{\"error\":\"" + errorMessage + "\"}"));
  }
}
