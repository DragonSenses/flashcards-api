package com.ken.flashcards.mapper;

import static java.util.UUID.randomUUID;

import org.springframework.stereotype.Component;

@Component
class IdGeneratorImpl implements IdGenerator {

  @Override
  public String generateId() {
    return randomUUID().toString();
  }

}
