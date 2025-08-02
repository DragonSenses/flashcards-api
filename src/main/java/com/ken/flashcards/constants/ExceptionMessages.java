package com.ken.flashcards.constants;

public final class ExceptionMessages {
  public static final String REQUEST_BODY_NULL = "Request body must not be null";
  public static final String FIELD_MUST_NOT_BE_NULL_OR_EMPTY = "%s must not be null or empty";

  public static final String CANNOT_FIND_FLASHCARD_BY_ID = "Flashcard with ID '%s' not found";

  public static final String CANNOT_FIND_STUDY_SESSION_BY_ID =
      "Study session with ID '%s' not found";

  public static final String CANNOT_FIND_STUDY_SESSION_BY_NAME =
      "Study session with name '%s' not found";

  public static final String CANNOT_FIND_STUDY_SESSION_BY_CATEGORY_ID =
      "Study session with category ID '%s' not found";

  private ExceptionMessages() {}
}
