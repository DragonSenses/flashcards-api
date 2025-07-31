package com.ken.flashcards.service;

import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

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

  private static final String CANNOT_FIND_BY_ID = "Flashcard with ID '%s' not found";

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

    NotFoundException ex = assertThrows(NotFoundException.class, 
        () -> flashcardService.findById(expectedFlashcardId));

    assertEquals(format(CANNOT_FIND_BY_ID, expectedFlashcardId), ex.getMessage());
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

}
