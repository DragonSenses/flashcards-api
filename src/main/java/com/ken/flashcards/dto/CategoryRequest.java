package com.ken.flashcards.dto;

import jakarta.validation.constraints.NotBlank;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true, access = PRIVATE)
@AllArgsConstructor
public class CategoryRequest {

  @NotBlank(message = "name is required")
  private final String name;

}
