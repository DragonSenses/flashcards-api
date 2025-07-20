package com.ken.flashcards.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ken.flashcards.dto.CategoryRequest;
import com.ken.flashcards.model.Category;

@Component
public class CategoryMapperImpl implements CategoryMapper {

  private final IdGenerator idGenerator;

  @Autowired
  public CategoryMapperImpl(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  public Category categoryFrom(CategoryRequest request) {
    return new Category(
      idGenerator.generateId(),
      request.getName()
    );
  }
}
