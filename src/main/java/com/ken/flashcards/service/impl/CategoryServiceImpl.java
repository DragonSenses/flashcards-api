package com.ken.flashcards.service.impl;

import java.util.Collection;
import com.ken.flashcards.model.Category;
import com.ken.flashcards.dto.CategoryRequest;
import com.ken.flashcards.mapper.CategoryMapper;
import com.ken.flashcards.repository.CategoryRepository;
import com.ken.flashcards.service.CategoryService;

public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  @Override
  public Collection<Category> findAll() {
    return categoryRepository.findAllByOrderByNameAsc();
  }

  @Override
  public Category createCategory(CategoryRequest request) {
    Category category = categoryMapper.categoryFrom(request);
    return categoryRepository.save(category);
  }
}
