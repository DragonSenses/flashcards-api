package com.ken.flashcards.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ken.flashcards.dto.CategoryRequest;
import com.ken.flashcards.mapper.CategoryMapper;
import com.ken.flashcards.model.Category;
import com.ken.flashcards.repository.CategoryRepository;
import com.ken.flashcards.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private CategoryMapper categoryMapper;

  @InjectMocks
  private CategoryServiceImpl categoryService;

  private List<Category> categories;

  private Category category;

  private CategoryRequest request;

  @BeforeEach
  void init() {
    this.request = new CategoryRequest("Art History");
    this.category = new Category("1", "Art History");
    this.categories = List.of(category);
  }

  @Test
  void findsAllOrderedByName() {
    when(categoryRepository.findAllByOrderByNameAsc()).thenReturn(categories);
    assertEquals(categories, categoryService.findAll());
    verify(categoryRepository, times(1)).findAllByOrderByNameAsc();
  }

}
