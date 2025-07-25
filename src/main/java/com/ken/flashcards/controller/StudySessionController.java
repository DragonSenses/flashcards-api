package com.ken.flashcards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.ok;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ken.flashcards.dto.StudySessionRequest;
import com.ken.flashcards.error.ResponseHandler;
import com.ken.flashcards.model.StudySession;
import com.ken.flashcards.service.StudySessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/sessions")
@Tag(name = "StudySession", description = "Operations related to study sessions")
public class StudySessionController implements ResponseHandler {

  private final StudySessionService studySessionService;

  @Autowired
  public StudySessionController(StudySessionService studySessionService) {
    this.studySessionService = studySessionService;
  }

  @Operation(summary = "Get all study sessions")
  @ApiResponse(responseCode = "200", description = "List of all study sessions")
  @GetMapping
  public ResponseEntity<Iterable<StudySession>> findAll() {
    return ok(studySessionService.findAll());
  }

  @Operation(summary = "Find a study session by ID")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Study session found"),
      @ApiResponse(responseCode = "404", description = "Study session not found")})
  @GetMapping("/{id}")
  public ResponseEntity<StudySession> findById(@PathVariable String id) {
    return response(studySessionService.findById(id), HttpStatus.OK);
  }

  @Operation(summary = "Find study sessions by category ID")
  @ApiResponse(responseCode = "200", description = "List of study sessions by category")
  @GetMapping("/category/{categoryId}")
  public ResponseEntity<Iterable<StudySession>> findByCategory(@PathVariable String categoryId) {
    return response(studySessionService.findAllByCategoryId(categoryId), HttpStatus.OK);
  }

  @Operation(summary = "Create a new study session")
  @ApiResponses({@ApiResponse(responseCode = "201", description = "Study session created"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "404", description ="Study session not found")})
  @PostMapping
  public ResponseEntity<StudySession> createStudySession(@RequestBody StudySessionRequest request) {
    StudySession session = studySessionService.createStudySession(request);
    return created(session);
  }

  @Operation(summary = "Upsert a study session")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Study session updated"),
      @ApiResponse(responseCode = "201", description = "Study session created"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "404", description = "Study session not found")})
  @PutMapping
  public ResponseEntity<StudySession> update(@Valid @RequestBody StudySession studySession) {
    return existsById(studySession.getId()) ? ResponseEntity.ok(save(studySession))
        : created(save(studySession));
  }

  @Operation(summary = "Delete a study session by ID")
  @ApiResponses({@ApiResponse(responseCode = "204", description = "Study session deleted"),
      @ApiResponse(responseCode = "404", description = "Study session not found")})
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    studySessionService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private boolean existsById(String id) {
    return studySessionService.existsById(id);
  }

  private StudySession save(StudySession studySession) {
    return studySessionService.save(studySession);
  }
}
