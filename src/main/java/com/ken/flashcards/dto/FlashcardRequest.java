package com.ken.flashcards.dto;

import jakarta.validation.constraints.NotBlank;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ken.flashcards.constants.ValidationMessages.STUDY_SESSION_ID_REQUIRED;
import static com.ken.flashcards.constants.ValidationMessages.QUESTION_REQUIRED;
import static com.ken.flashcards.constants.ValidationMessages.ANSWER_REQUIRED;

@Data
@NoArgsConstructor(force = true, access = PRIVATE)
@AllArgsConstructor
public class FlashcardRequest {

  @NotBlank(message = STUDY_SESSION_ID_REQUIRED)
  private final String studySessionId;

  @NotBlank(message = QUESTION_REQUIRED)
  private final String question;

  @NotBlank(message = ANSWER_REQUIRED)
  private final String answer;

}
