package com.ken.flashcards.dto;

import jakarta.validation.constraints.NotBlank;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ken.flashcards.constants.ValidationMessages.CATEGORY_ID_REQUIRED;
import static com.ken.flashcards.constants.ValidationMessages.NAME_REQUIRED;

@Data
@NoArgsConstructor(force = true, access = PRIVATE)
@AllArgsConstructor
public class StudySessionRequest {

  @NotBlank(message = CATEGORY_ID_REQUIRED)
  private final String categoryId;

  @NotBlank(message = NAME_REQUIRED)
  private final String name;

}
