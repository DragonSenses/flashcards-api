package com.ken.flashcards.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Category {

  @Id
  @NotBlank(message = "id is required")
  private final String id;

  @NotBlank(message = "name is required")
  private final String name;

}
