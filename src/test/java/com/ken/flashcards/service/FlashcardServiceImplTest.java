package com.ken.flashcards.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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


}
