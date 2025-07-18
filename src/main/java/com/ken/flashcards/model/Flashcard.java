package com.ken.flashcards.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class Flashcard {

  @Id
  @NotBlank(message = "id is required")
  private final String id;

  @NotBlank(message = "study session id is required")
  private final String studySessionId;

  @NotBlank(message = "question is required")
  private final String question;

  @NotBlank(message = "answer is required")
  private final String answer;

}
