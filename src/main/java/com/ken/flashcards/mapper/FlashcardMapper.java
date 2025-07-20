package com.ken.flashcards.mapper;

import com.ken.flashcards.dto.FlashcardRequest;
import com.ken.flashcards.model.Flashcard;

public interface FlashcardMapper {

  Flashcard flashcardFrom(FlashcardRequest request);

}
