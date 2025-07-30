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

class StudySessionRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldFailValidationWhenCategoryIdIsBlank() {
    StudySessionRequest request = new StudySessionRequest("  ", "Art History");
    Set<ConstraintViolation<StudySessionRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertEquals("category id is required", violations.iterator().next().getMessage());
  }

  @Test
  void shouldFailValidationWhenNameIsBlank() {
    StudySessionRequest request = new StudySessionRequest("1", "   ");
    Set<ConstraintViolation<StudySessionRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertEquals("name is required", violations.iterator().next().getMessage());
  }

  @Test
  void shouldPassValidationWhenFieldsAreValid() {
    StudySessionRequest request = new StudySessionRequest("1", "Art History");
    Set<ConstraintViolation<StudySessionRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty());
  }
}
