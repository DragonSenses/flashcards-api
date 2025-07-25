package com.ken.flashcards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ken.flashcards.dto.StudySessionRequest;
import com.ken.flashcards.model.StudySession;
import com.ken.flashcards.service.StudySessionService;
import com.ken.flashcards.error.ResponseHandler;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/sessions")
public class StudySessionController implements ResponseHandler {

  private final StudySessionService studySessionService;

  @Autowired
  public StudySessionController(StudySessionService studySessionService) {
    this.studySessionService = studySessionService;
  }

  @GetMapping
  public ResponseEntity<Collection<StudySession>> findAll() {
    return response(studySessionService.findAll(), org.springframework.http.HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<StudySession> findById(@PathVariable String id) {
    return response(studySessionService.findById(id), org.springframework.http.HttpStatus.OK);
  }

  @GetMapping("/category/{categoryId}")
  public ResponseEntity<Iterable<StudySession>> findByCategory(@PathVariable String categoryId) {
    return response(studySessionService.findAllByCategoryId(categoryId), org.springframework.http.HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<StudySession> createStudySession(@RequestBody StudySessionRequest request) {
    StudySession session = studySessionService.createStudySession(request);
    return created(session);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    studySessionService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

}