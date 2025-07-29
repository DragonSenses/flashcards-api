package com.ken.flashcards.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class IdGeneratorImplTest {

  @Test
  void generatedIdIsNotNull() {
    IdGenerator idGenerator = new IdGeneratorImpl();
    String id = idGenerator.generateId();
    assertNotNull(id);
  }

  @Test
  void generatedIdIsNotEmpty() {
    IdGenerator idGenerator = new IdGeneratorImpl();
    String id = idGenerator.generateId();
    assertFalse(id.trim().isEmpty());
  }

  @Test
  void generatedIdHasDesiredLength() {
    IdGenerator idGenerator = new IdGeneratorImpl();
    String id = idGenerator.generateId();
    // UUID string format is always 36 characters (8-4-4-4-12) with hyphens
    assertEquals(36, id.length());
  }

}
