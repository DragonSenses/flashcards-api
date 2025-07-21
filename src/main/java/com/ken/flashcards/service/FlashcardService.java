package com.ken.flashcards.service;

import com.ken.flashcards.dto.FlashcardRequest;
import com.ken.flashcards.model.Flashcard;

public interface FlashcardService {
  Iterable<Flashcard> findAll();

  Flashcard findById(String id);

  Flashcard createFlashcard(FlashcardRequest request);

  boolean existsById(String id);

  Flashcard save(Flashcard flashcard);

  void deleteById(String id);

  Iterable<Flashcard> findAllByStudySessionId(String studySessionId);

}
