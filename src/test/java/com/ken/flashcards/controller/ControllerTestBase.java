package com.ken.flashcards.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract base class for controller-layer tests.
 * <p>
 * Provides common utilities such as JSON serialization using {@link ObjectMapper}.
 * Extend this class in any {@code @WebMvcTest} to simplify response verification.
 */
public abstract class ControllerTestBase {

  private final ObjectMapper objectMapper = new ObjectMapper();

  protected String serialize(Object object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }

  protected <T> T deserialize(String json, Class<T> type) throws JsonProcessingException {
    return objectMapper.readValue(json, type);
  }
}
