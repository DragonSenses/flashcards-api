package com.ken.flashcards.service;

import com.ken.flashcards.exception.BadRequestException;

public abstract class ValidatingService {

  protected void assertNotNull(Object object) {
    if (object == null) {
      throw new BadRequestException("Request body must not be null");
    }
  }

  protected void assertNotBlank(String value, String fieldName) {
    if (value == null || value.trim().isEmpty()) {
      throw new BadRequestException(fieldName + " must not be blank");
    }
  }

  // Add more reusable validation helpers as needed
}
