package com.ken.flashcards.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ken.flashcards.dto.FlashcardRequest;
import com.ken.flashcards.model.Flashcard;

@ExtendWith(MockitoExtension.class)
public class FlashcardMapperTest {

  @Mock
  private IdGenerator idGenerator;

  @InjectMocks
  private FlashcardMapperImpl flashcardMapper;

  @Test
  void testFlashcardFrom() {
    String studySessionId = "42";
    FlashcardRequest request =
        new FlashcardRequest(studySessionId, "What is the capital of France?", "Paris");

    when(idGenerator.generateId()).thenReturn("flashcard-001");
    Flashcard flashcard = flashcardMapper.flashcardFrom(request);

    assertEquals("flashcard-001", flashcard.getId());
    assertEquals("42", flashcard.getStudySessionId());
    assertEquals("What is the capital of France?", flashcard.getQuestion());
    assertEquals("Paris", flashcard.getAnswer());
    verify(idGenerator, times(1)).generateId();
  }
}
