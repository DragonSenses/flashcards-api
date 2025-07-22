package com.ken.flashcards.service.impl;

import org.springframework.stereotype.Service;
import com.ken.flashcards.dto.FlashcardRequest;
import com.ken.flashcards.mapper.FlashcardMapper;
import com.ken.flashcards.model.Flashcard;
import com.ken.flashcards.repository.FlashcardRepository;
import com.ken.flashcards.service.FlashcardService;

@Service
public class FlashcardServiceImpl implements FlashcardService {

  private final FlashcardRepository repository;
  private final FlashcardMapper mapper;

  public FlashcardServiceImpl(FlashcardRepository repository, FlashcardMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  public Flashcard create(FlashcardRequest request) {
    Flashcard flashcard = mapper.flashcardFrom(request);
    return repository.save(flashcard);
  }

  public Iterable<Flashcard> findBySession(String sessionId) {
    return repository.findAllByStudySessionId(sessionId);
  }
}