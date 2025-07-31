package com.ken.flashcards.service.impl;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ken.flashcards.dto.FlashcardRequest;
import com.ken.flashcards.exception.NotFoundException;
import com.ken.flashcards.mapper.FlashcardMapper;
import com.ken.flashcards.model.Flashcard;
import com.ken.flashcards.repository.FlashcardRepository;
import com.ken.flashcards.service.FlashcardService;
import com.ken.flashcards.service.StudySessionService;
import com.ken.flashcards.service.ValidatingService;

@Service
@Transactional
public class FlashcardServiceImpl extends ValidatingService implements FlashcardService {

  private final FlashcardRepository repository;
  private final StudySessionService studySessionService;
  private final FlashcardMapper mapper;

  private static final String CANNOT_FIND_BY_ID = "Flashcard with ID '%s' not found";

  @Autowired
  public FlashcardServiceImpl(FlashcardRepository repository,
      StudySessionService studySessionService, FlashcardMapper mapper) {
    this.repository = repository;
    this.studySessionService = studySessionService;
    this.mapper = mapper;
  }

  @Override
  public Iterable<Flashcard> findAll() {
    return repository.findAll();
  }

  @Override
  public Flashcard findById(String id) {
    return repository.findById(id)
        .orElseThrow(() -> new NotFoundException(format(CANNOT_FIND_BY_ID, id)));
  }

  @Override
  public Flashcard createFlashcard(FlashcardRequest request) {
    validate(request);
    Flashcard flashcard = flashcardFrom(request);
    return repository.save(flashcard);
  }

  @Override
  public boolean existsById(String id) {
    return repository.existsById(id);
  }

  @Override
  public Flashcard save(Flashcard flashcard) {
    validate(flashcard);
    return repository.save(flashcard);
  }

  @Override
  public void deleteById(String id) {
    repository.deleteById(id);
  }

  @Override
  public Iterable<Flashcard> findAllByStudySessionId(String studySessionId) {
    studySessionService.assertExistsById(studySessionId);
    return repository.findAllByStudySessionId(studySessionId);
  }

  private void validate(FlashcardRequest request) {
    assertNotNull(request);
    studySessionService.assertExistsById(request.getStudySessionId());
  }

  private void validate(Flashcard flashcard) {
    assertNotNull(flashcard);
    studySessionService.assertExistsById(flashcard.getStudySessionId());
  }

  private Flashcard flashcardFrom(FlashcardRequest request) {
    return mapper.flashcardFrom(request);
  }

}
