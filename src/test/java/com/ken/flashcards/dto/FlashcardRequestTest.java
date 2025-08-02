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

import static com.ken.flashcards.constants.ValidationMessages.STUDY_SESSION_ID_REQUIRED;
import static com.ken.flashcards.constants.ValidationMessages.QUESTION_REQUIRED;
import static com.ken.flashcards.constants.ValidationMessages.ANSWER_REQUIRED;

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
    assertEquals(STUDY_SESSION_ID_REQUIRED, violations.iterator().next().getMessage());
  }

  @Test
  void shouldFailValidationWhenQuestionIsBlank() {
    FlashcardRequest request = new FlashcardRequest("123", "   ", "Inheritance in Java");
    Set<ConstraintViolation<FlashcardRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertEquals(QUESTION_REQUIRED, violations.iterator().next().getMessage());
  }

  @Test
  void shouldFailValidationWhenAnswerIsBlank() {
    FlashcardRequest request = new FlashcardRequest("123", "Define encapsulation", "  ");
    Set<ConstraintViolation<FlashcardRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertEquals(ANSWER_REQUIRED, violations.iterator().next().getMessage());
  }

  @Test
  void shouldPassValidationWhenAllFieldsAreValid() {
    FlashcardRequest request = new FlashcardRequest("123", "What is abstraction?", "Hiding implementation details");
    Set<ConstraintViolation<FlashcardRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty());
  }
}