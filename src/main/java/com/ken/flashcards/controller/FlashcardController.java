package com.ken.flashcards.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.ken.flashcards.dto.FlashcardRequest;
import com.ken.flashcards.error.ErrorResponse;
import com.ken.flashcards.error.ResponseHandler;
import com.ken.flashcards.model.Flashcard;
import com.ken.flashcards.service.FlashcardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/flashcards")
@Tag(name = "Flashcard", description = "Operations related to flashcard resources")
public class FlashcardController implements ResponseHandler {

  private final FlashcardService flashcardService;

  @Autowired
  public FlashcardController(FlashcardService flashcardService) {
    this.flashcardService = flashcardService;
  }

  @Operation(summary = "Get all flashcards")
  @ApiResponse(responseCode = "200", description = "Retrieved all flashcards",
      content = @Content(mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = Flashcard.class))))
  @GetMapping
  public ResponseEntity<Iterable<Flashcard>> findAll() {
    return ok(flashcardService.findAll());
  }

  @Operation(summary = "Find flashcard by ID")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Flashcard found",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Flashcard.class))),
    @ApiResponse(responseCode = "404", description = "Flashcard not found",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{id}")
  public ResponseEntity<Flashcard> findById(@PathVariable String id) {
    return ok(flashcardService.findById(id));
  }

  @Operation(summary = "Get all flashcards by study session ID",
      parameters = @Parameter(name = "studySessionId", description = "ID of the study session", required = true))
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Flashcards retrieved by session",
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = Flashcard.class)))),
    @ApiResponse(responseCode = "404", description = "Study session not found",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/session/{studySessionId}")
  public ResponseEntity<Iterable<Flashcard>> findBySession(@PathVariable String studySessionId) {
    return ok(flashcardService.findAllByStudySessionId(studySessionId));
  }

  @Operation(summary = "Create a flashcard")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Flashcard created",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Flashcard.class))),
    @ApiResponse(responseCode = "400", description = "Invalid request data",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse( responseCode = "404", description = "Study session not found",
          content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class))})
  })
  @PostMapping
  public ResponseEntity<Flashcard> createFlashcard(@RequestBody FlashcardRequest request) {
    Flashcard flashcard = flashcardService.createFlashcard(request);
    return created(flashcard);
  }

  @Operation(summary = "Update or create a flashcard")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Flashcard updated",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Flashcard.class))),
    @ApiResponse(responseCode = "201", description = "Flashcard created",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Flashcard.class))),
    @ApiResponse(responseCode = "400", description = "Invalid flashcard data",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse( responseCode = "404", description = "Study session not found",
          content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class))})
  })
  @PutMapping
  public ResponseEntity<Flashcard> update(@Valid @RequestBody Flashcard flashcard) {
    return existsById(flashcard.getId())
        ? ok(save(flashcard))
        : created(save(flashcard));
  }

  @Operation(summary = "Delete a flashcard by ID")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Flashcard deleted"),
    @ApiResponse(responseCode = "404", description = "Flashcard not found",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
  })
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