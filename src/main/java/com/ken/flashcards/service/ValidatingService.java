package com.ken.flashcards.service;

import static java.lang.String.format;

import static com.ken.flashcards.constants.ExceptionMessages.FIELD_MUST_NOT_BE_NULL_OR_EMPTY;
import static com.ken.flashcards.constants.ExceptionMessages.REQUEST_BODY_NULL;
import com.ken.flashcards.exception.BadRequestException;

public abstract class ValidatingService {

  /**
   * Throws BadRequestException if the object is null.
   */
  public void assertNotNull(Object object) {
    if (object == null) {
      throw new BadRequestException(REQUEST_BODY_NULL);
    }
  }

  /**
   * Throws BadRequestException if the string is null or blank.
   * 
   * @param fieldName used in the exception message
   */
  public void assertNotBlank(String value, String fieldName) {
    if (value == null || value.trim().isEmpty()) {
      throw new BadRequestException(format(FIELD_MUST_NOT_BE_NULL_OR_EMPTY, fieldName));
    }
  }

  // Add more reusable validation helpers as needed
}
