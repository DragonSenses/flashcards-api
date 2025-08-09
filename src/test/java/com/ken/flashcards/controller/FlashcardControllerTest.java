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
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ken.flashcards.constants.ExceptionMessages;
import com.ken.flashcards.dto.FlashcardRequest;
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

  @DisplayName("GET /api/v1/flashcards - should return 200 with all flashcards")
  @Test
  void shouldReturn200WithAllFlashcards() throws Exception {
    when(flashcardService.findAll()).thenReturn(Set.of(flashcard));

    mockMvc.perform(get(flashcardsPath)).andExpect(status().isOk())
        .andExpect(content().json(serialize(Set.of(flashcard))));
  }

  @DisplayName("GET /api/v1/flashcards/{id} - should return 200 when flashcard exists")
  @Test
  void shouldReturn200WhenFlashcardExistsById() throws Exception {
    when(flashcardService.findById(expectedFlashcardId)).thenReturn(flashcard);

    mockMvc.perform(get(flashcardsPath + "/" + expectedFlashcardId)).andExpect(status().isOk())
        .andExpect(content().json(serialize(flashcard)));
  }

  @DisplayName("GET /api/v1/flashcards/{id} - should return 404 when flashcard is not found")
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

  @DisplayName("GET /api/v1/flashcards/details?studySessionId={id} - should return 200 when study session exists")
  @Test
  void shouldReturn200WhenStudySessionExists() throws Exception {
    when(flashcardService.findAllByStudySessionId(expectedStudySessionId))
        .thenReturn(Set.of(flashcard));

    mockMvc.perform(get(flashcardsPath + "/details?studySessionId=" + expectedStudySessionId))
        .andExpect(status().isOk()).andExpect(content().json(serialize(Set.of(flashcard))));
  }

  @DisplayName("GET /api/v1/flashcards/details - should return 404 when study session is not found")
  @Test
  void shouldReturn404WhenStudySessionNotFound() throws Exception {
    String studySessionId = "nonexistent-session-id";
    String errorMessage = "Study session not found: " + studySessionId;

    when(flashcardService.findAllByStudySessionId(studySessionId))
        .thenThrow(new NotFoundException(errorMessage));

    mockMvc.perform(get("/api/v1/flashcards/details").param("studySessionId", studySessionId))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
        .andExpect(content().json("{\"error\":\"" + errorMessage + "\"}"));
  }

  @DisplayName("POST /api/v1/flashcards - should create a new flashcard")
  @Test
  void shouldCreateFlashcard() throws Exception {
    FlashcardRequest request =
        new FlashcardRequest(expectedStudySessionId, expectedQuestion, expectedAnswer);

    when(flashcardService.createFlashcard(request)).thenReturn(flashcard);

    mockMvc.perform(post(flashcardsPath).contentType(APPLICATION_JSON).content(serialize(request)))
        .andExpect(status().isCreated()).andExpect(content().json(serialize(flashcard)));
  }

  @DisplayName("POST /api/v1/flashcards - should return 400 when request body is empty")
  @Test
  void shouldReturnBadRequestWhenRequestBodyIsEmpty() throws Exception {
    mockMvc.perform(post(flashcardsPath).contentType(APPLICATION_JSON).content(""))
        .andExpect(status().isBadRequest());
  }

  @DisplayName("POST /api/v1/flashcards - should return 404 when study session ID does not exist")
  @Test
  void shouldReturnNotFoundWhenStudySessionIdDoesNotExistOnCreate() throws Exception {
    String nonexistentStudySessionId = "nonexistent-study-session-id-999";
    String errorMessage =
        String.format(ExceptionMessages.CANNOT_FIND_STUDY_SESSION_BY_ID, nonexistentStudySessionId);

    FlashcardRequest request =
        new FlashcardRequest(nonexistentStudySessionId, "What is the unit of charge?", "Coulomb");

    when(flashcardService.createFlashcard(request)).thenThrow(new NotFoundException(errorMessage));

    mockMvc.perform(post(flashcardsPath).contentType(APPLICATION_JSON).content(serialize(request)))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\"error\":\"" + errorMessage + "\"}"));
  }

  @DisplayName("PUT /api/v1/flashcards - should update flashcard when ID exists")
  @Test
  void shouldUpdateFlashcardWhenIdExists() throws Exception {
    when(flashcardService.existsById(flashcard.getId())).thenReturn(true);
    when(flashcardService.save(flashcard)).thenReturn(flashcard);

    mockMvc.perform(put(flashcardsPath).contentType(APPLICATION_JSON).content(serialize(flashcard)))
        .andExpect(status().isOk()).andExpect(content().json(serialize(flashcard)));
  }

  @DisplayName("PUT /api/v1/flashcards - should create flashcard when ID does not exist (upsert)")
  @Test
  void shouldCreateFlashcardWhenIdDoesNotExist() throws Exception {
    when(flashcardService.existsById(flashcard.getId())).thenReturn(false);
    when(flashcardService.save(flashcard)).thenReturn(flashcard);

    mockMvc.perform(put(flashcardsPath).contentType(APPLICATION_JSON).content(serialize(flashcard)))
        .andExpect(status().isCreated()).andExpect(content().json(serialize(flashcard)));
  }

  @DisplayName("PUT /api/v1/flashcards - should return 400 when request body is empty")
  @Test
  void shouldReturnBadRequestWhenUpdatePayloadIsEmpty() throws Exception {
    mockMvc.perform(put(flashcardsPath).contentType(APPLICATION_JSON).content(""))
        .andExpect(status().isBadRequest());
  }

  @DisplayName("PUT /api/v1/flashcards - should return 400 when request body is missing")
  @Test
  void shouldReturnBadRequestWhenUpdatePayloadIsMissing() throws Exception {
    mockMvc.perform(put(flashcardsPath).contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @DisplayName("PUT /api/v1/flashcards - should return 404 when study session ID does not exist during update")
  @Test
  void shouldReturnNotFoundWhenUpdatingFlashcardWithNonexistentStudySession() throws Exception {
    String nonexistentStudySessionId = "nonexistent-study-session-id-999";
    String errorMessage =
        String.format(ExceptionMessages.CANNOT_FIND_STUDY_SESSION_BY_ID, nonexistentStudySessionId);

    Flashcard newFlashcard = new Flashcard("1", nonexistentStudySessionId,
        "Is water (H₂O) ionic or covalent?", "Covalent");

    when(flashcardService.existsById("1")).thenReturn(true);
    when(flashcardService.save(newFlashcard)).thenThrow(new NotFoundException(errorMessage));

    mockMvc
        .perform(put(flashcardsPath).contentType(APPLICATION_JSON).content(serialize(newFlashcard)))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\"error\":\"" + errorMessage + "\"}"));
  }


}
