package com.ken.flashcards.error;

import lombok.Data;

@Data
public class ErrorResponse {

  private final String error;

  public static ErrorResponse withMessage(String message) {
    return new ErrorResponse(message);
  }

}
