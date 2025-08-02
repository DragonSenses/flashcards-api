package com.ken.flashcards.dto;

import jakarta.validation.constraints.NotBlank;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ken.flashcards.constants.ValidationMessages.NAME_REQUIRED;

@Data
@NoArgsConstructor(force = true, access = PRIVATE)
@AllArgsConstructor
public class CategoryRequest {

  @NotBlank(message = NAME_REQUIRED)
  private final String name;

}
