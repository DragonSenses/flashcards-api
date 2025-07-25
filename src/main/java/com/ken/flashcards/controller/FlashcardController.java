package com.ken.flashcards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ken.flashcards.dto.FlashcardRequest;
import com.ken.flashcards.error.ResponseHandler;
import com.ken.flashcards.model.Flashcard;
import com.ken.flashcards.service.FlashcardService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/flashcards")
public class FlashcardController implements ResponseHandler {

  private final FlashcardService flashcardService;

  @Autowired
  public FlashcardController(FlashcardService flashcardService) {
    this.flashcardService = flashcardService;
  }

  @GetMapping
  public ResponseEntity<Iterable<Flashcard>> findAll() {
    return response(flashcardService.findAll(), org.springframework.http.HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Flashcard> findById(@PathVariable String id) {
    return response(flashcardService.findById(id), org.springframework.http.HttpStatus.OK);
  }

  @GetMapping("/session/{studySessionId}")
  public ResponseEntity<Iterable<Flashcard>> findBySession(@PathVariable String studySessionId) {
    return response(flashcardService.findAllByStudySessionId(studySessionId), org.springframework.http.HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<Flashcard> createFlashcard(@RequestBody FlashcardRequest request) {
    Flashcard flashcard = flashcardService.createFlashcard(request);
    return created(flashcard);
  }

  @PutMapping
  public ResponseEntity<Flashcard> update(@Valid @RequestBody Flashcard flashcard) {
    return existsById(flashcard.getId())
        ? ResponseEntity.ok(save(flashcard))
        : created(save(flashcard));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    flashcardService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private boolean existsById(String id) {
    return flashcardService.existsById(id);
  }

  private Flashcard save(Flashcard flashcard) {
    return flashcardService.save(flashcard);
  }
}