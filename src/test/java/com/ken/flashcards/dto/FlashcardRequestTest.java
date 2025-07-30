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

class FlashcardRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldFailValidationWhenStudySessionIdIsBlank() {
    FlashcardRequest request = new FlashcardRequest("   ", "What is polymorphism?", "Ability of objects to take many forms");
    Set<ConstraintViolation<FlashcardRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertEquals("study session id is required", violations.iterator().next().getMessage());
  }

  @Test
  void shouldFailValidationWhenQuestionIsBlank() {
    FlashcardRequest request = new FlashcardRequest("123", "   ", "Inheritance in Java");
    Set<ConstraintViolation<FlashcardRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertEquals("question is required", violations.iterator().next().getMessage());
  }

  @Test
  void shouldFailValidationWhenAnswerIsBlank() {
    FlashcardRequest request = new FlashcardRequest("123", "Define encapsulation", "  ");
    Set<ConstraintViolation<FlashcardRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertEquals("answer is required", violations.iterator().next().getMessage());
  }

  @Test
  void shouldPassValidationWhenAllFieldsAreValid() {
    FlashcardRequest request = new FlashcardRequest("123", "What is abstraction?", "Hiding implementation details");
    Set<ConstraintViolation<FlashcardRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty());
  }
}