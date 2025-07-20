package com.ken.flashcards.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ken.flashcards.dto.FlashcardRequest;
import com.ken.flashcards.model.Flashcard;

@Component
public class FlashcardMapperImpl implements FlashcardMapper {

  private final IdGenerator idGenerator;

  @Autowired
  public FlashcardMapperImpl(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  public Flashcard flashcardFrom(FlashcardRequest request) {
    return new Flashcard(
      idGenerator.generateId(),
      request.getStudySessionId(),
      request.getQuestion(),
      request.getAnswer()
    );
  }
}
