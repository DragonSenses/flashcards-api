package com.ken.flashcards.dto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static com.ken.flashcards.constants.ValidationMessages.NAME_REQUIRED;

class CategoryRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldFailValidationWhenNameIsBlank() {
    CategoryRequest request = new CategoryRequest("  "); // invalid name
    Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());
    assertEquals(NAME_REQUIRED, violations.iterator().next().getMessage());
  }

  @Test
  void shouldPassValidationWhenNameIsValid() {
    CategoryRequest request = new CategoryRequest("Languages");
    Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty());
  }
}
