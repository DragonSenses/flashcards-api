package com.ken.flashcards.service;

import com.ken.flashcards.dto.CategoryRequest;
import com.ken.flashcards.model.Category;

public interface CategoryService {

  Iterable<Category> findAll();

  Category findById(String id);

  Category createCategory(CategoryRequest request);

  void deleteById(String id);

  boolean existsById(String id);

  Category save(Category category);

  void assertExistsById(String id);

  Category findByName(String name);

  String idFromCategoryWithName(String categoryName);

}
