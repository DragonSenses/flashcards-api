package com.ken.flashcards.service;

import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ken.flashcards.constants.ExceptionMessages.CANNOT_FIND_FLASHCARD_BY_ID;
import static com.ken.flashcards.constants.ExceptionMessages.CANNOT_FIND_STUDY_SESSION_BY_ID;
import com.ken.flashcards.dto.FlashcardRequest;
import com.ken.flashcards.exception.NotFoundException;
import com.ken.flashcards.mapper.FlashcardMapper;
import com.ken.flashcards.model.Flashcard;
import com.ken.flashcards.repository.FlashcardRepository;
import com.ken.flashcards.service.impl.FlashcardServiceImpl;

@ExtendWith(MockitoExtension.class)
public class FlashcardServiceImplTest {

  @Mock
  private FlashcardRepository flashcardRepository;

  @Mock
  private StudySessionService studySessionService;

  @Mock
  private FlashcardMapper mapper;

  @InjectMocks
  private FlashcardServiceImpl flashcardService;

  private Flashcard flashcard;

  private List<Flashcard> flashcards;

  private final String expectedFlashcardId = "flashcard-001";
  private final String expectedStudySessionId = "session-002";
  private final String expectedQuestion = "Why is the sky blue?";
  private final String expectedAnswer = "Rayleigh Scattering";

  @BeforeEach
  void init() {
    this.flashcard = new Flashcard(expectedFlashcardId, expectedStudySessionId, expectedQuestion,
        expectedAnswer);
    this.flashcards = List.of(flashcard);
  }

  // findAll()
  // Verifies that all flashcards are retrieved from the repository
  @Test
  void shouldReturnAllFlashcardsFromRepository() {
    when(flashcardRepository.findAll()).thenReturn(flashcards);

    assertEquals(flashcards, flashcardService.findAll());
    verify(flashcardRepository, times(1)).findAll();
  }

  // findById()
  // Ensures a valid flashcard ID returns the correct flashcard
  @Test
  void shouldReturnFlashcardByIdWhenPresent() {
    when(flashcardRepository.findById(expectedFlashcardId)).thenReturn(Optional.of(flashcard));

    assertEquals(flashcard, flashcardService.findById(expectedFlashcardId));
    verify(flashcardRepository, times(1)).findById(expectedFlashcardId);
  }

  // findById()
  // Throws NotFoundException when flashcard ID is not found
  @Test
  void shouldThrowExceptionWhenFlashcardDoesNotExistById() {
    when(flashcardRepository.findById(expectedFlashcardId)).thenReturn(Optional.empty());

    NotFoundException ex =
        assertThrows(NotFoundException.class, () -> flashcardService.findById(expectedFlashcardId));

    assertEquals(format(CANNOT_FIND_FLASHCARD_BY_ID, expectedFlashcardId), ex.getMessage());
    verify(flashcardRepository, times(1)).findById(expectedFlashcardId);
  }

  // findAllByStudySessionId()
  // Retrieves all flashcards associated with a specific study session
  @Test
  void shouldReturnFlashcardsByStudySessionId() {
    when(flashcardRepository.findAllByStudySessionId(expectedStudySessionId))
        .thenReturn(Set.of(flashcard));

    assertEquals(Set.of(flashcard),
        flashcardService.findAllByStudySessionId(expectedStudySessionId));
    verify(flashcardRepository, times(1)).findAllByStudySessionId(expectedStudySessionId);
  }

  // createFlashcard()
  // Verifies that a flashcard is created when the study session exists
  @Test
  void shouldCreateFlashcardWhenStudySessionExists() {
    FlashcardRequest request =
        new FlashcardRequest(expectedStudySessionId, expectedQuestion, expectedAnswer);
    Flashcard newFlashcard =
        new Flashcard("new-id", expectedStudySessionId, expectedQuestion, expectedAnswer);

    doNothing().when(studySessionService).assertExistsById(expectedStudySessionId);
    when(mapper.flashcardFrom(request)).thenReturn(newFlashcard);
    when(flashcardRepository.save(newFlashcard)).thenReturn(newFlashcard);

    assertEquals(newFlashcard, flashcardService.createFlashcard(request));
    verify(mapper, times(1)).flashcardFrom(request);
    verify(studySessionService, times(1)).assertExistsById(expectedStudySessionId);
    verify(flashcardRepository, times(1)).save(newFlashcard);
  }

  // createFlashcard()
  // Throws NotFoundException when creating a flashcard with a missing study session
  @Test
  void shouldThrowExceptionWhenCreatingFlashcardWithInvalidStudySessionId() {
    String invalidStudySessionId = "invalid-id-123";
    FlashcardRequest request =
        new FlashcardRequest(invalidStudySessionId, expectedQuestion, expectedAnswer);

    doThrow(new NotFoundException(format(CANNOT_FIND_STUDY_SESSION_BY_ID, invalidStudySessionId)))
        .when(studySessionService).assertExistsById(invalidStudySessionId);

    NotFoundException ex =
        assertThrows(NotFoundException.class, () -> flashcardService.createFlashcard(request));
    assertEquals(format(CANNOT_FIND_STUDY_SESSION_BY_ID, invalidStudySessionId), ex.getMessage());

    verify(studySessionService, times(1)).assertExistsById(invalidStudySessionId);
  }

  // existsById()
  // Returns true when flashcard with given ID exists
  @Test
  void shouldReturnTrueWhenFlashcardExistsById() {
    when(flashcardRepository.existsById(expectedFlashcardId)).thenReturn(true);

    assertTrue(flashcardService.existsById(expectedFlashcardId));
    verify(flashcardRepository, times(1)).existsById(expectedFlashcardId);
  }

  // save()
  // Persists a flashcard and returns the saved entity
  @Test
  void shouldSaveFlashcardSuccessfully() {
    when(flashcardRepository.save(flashcard)).thenReturn(flashcard);

    assertEquals(flashcard, flashcardService.save(flashcard));
    verify(flashcardRepository, times(1)).save(flashcard);
  }

}
