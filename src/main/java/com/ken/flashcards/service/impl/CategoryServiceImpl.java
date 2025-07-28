package com.ken.flashcards.service.impl;

import static java.lang.String.format;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ken.flashcards.dto.CategoryRequest;
import com.ken.flashcards.exception.ConflictException;
import com.ken.flashcards.exception.NotFoundException;
import com.ken.flashcards.mapper.CategoryMapper;
import com.ken.flashcards.model.Category;
import com.ken.flashcards.repository.CategoryRepository;
import com.ken.flashcards.service.CategoryService;
import com.ken.flashcards.service.ValidatingService;

@Service
@Transactional
public class CategoryServiceImpl extends ValidatingService implements CategoryService {

  private static final String CANNOT_FIND_BY_ID = "Cannot find category with id = %s";
  private static final String CANNOT_FIND_BY_NAME = "Cannot find category with name = %s";

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Autowired
  public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  @Override
  public Collection<Category> findAll() {
    return categoryRepository.findAllByOrderByNameAsc();
  }

  @Override
  public Category findById(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("Id must not be null or empty");
    }
    return categoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(format(CANNOT_FIND_BY_ID, id)));
  }

  @Override
  public Category createCategory(CategoryRequest request) {
    validate(request);
    Category category = categoryMapper.categoryFrom(request);
    return categoryRepository.save(category);
  }

  @Override
  public void deleteById(String id) {
    assertExistsById(id);
    categoryRepository.deleteById(id);
  }

  @Override
  public boolean existsById(String id) {
    return categoryRepository.existsById(id);
  }

  @Override
  public Category save(Category category) {
    validate(category);
    return categoryRepository.save(category);
  }

  @Override
  public void assertExistsById(String id) {
    if (!existsById(id)) {
      throw new NotFoundException(format(CANNOT_FIND_BY_ID, id));
    }
  }

  @Override
  public Category findByName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name must not be null or empty");
    }
    return categoryRepository.findByName(name)
        .orElseThrow(() -> new NotFoundException(format(CANNOT_FIND_BY_NAME, name)));
  }

  @Override
  public String idFromCategoryWithName(String categoryName) {
    return findByName(categoryName).getId();
  }

  private void validate(Category category) {
    assertNotNull(category);
    assertDoesNotExistByName(category.getName());
  }

  private void validate(CategoryRequest request) {
    assertNotNull(request);
    assertDoesNotExistByName(request.getName());
  }

  private void assertDoesNotExistByName(String name) {
    if (categoryRepository.existsByName(name)) {
      throw new ConflictException(format("Category with name = %s already exists", name));
    }
  }

}
