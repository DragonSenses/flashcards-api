package com.ken.flashcards.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.ken.flashcards.exception.NotFoundException;
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
  void findAllOrderedByName() {
    when(categoryRepository.findAllByOrderByNameAsc()).thenReturn(categories);
    assertEquals(categories, categoryService.findAll());
    verify(categoryRepository, times(1)).findAllByOrderByNameAsc();
  }

  @Test
  void returnEmptyListWhenNoCategoriesExist() {
    when(categoryRepository.findAllByOrderByNameAsc()).thenReturn(List.of());
    assertEquals(List.of(), categoryService.findAll());
    verify(categoryRepository, times(1)).findAllByOrderByNameAsc();
  }

  @Test
  void findCategoryById() {
    when(categoryRepository.findById("1")).thenReturn(Optional.of(category));
    assertEquals(category, categoryService.findById("1"));
    verify(categoryRepository, times(1)).findById("1");
  }

  @Test
  void findByIdThrowsExceptionWhenIdIsNull() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> categoryService.findById(null));
    assertEquals("Id must not be null or empty", ex.getMessage());
  }

  @Test
  void findByIdThrowsExceptionWhenIdIsEmpty() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> categoryService.findById(""));
    assertEquals("Id must not be null or empty", ex.getMessage());
  }

  @Test
  void findByIdThrowsExceptionWhenIdIsWhitespace() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> categoryService.findById("   "));
    assertEquals("Id must not be null or empty", ex.getMessage());
  }

  @Test
  void findByIdPropagatesRepositoryException() {
    when(categoryRepository.findById("1")).thenThrow(new RuntimeException("DB unavailable"));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> categoryService.findById("1"));
    assertEquals("DB unavailable", ex.getMessage());
  }

  @Test
  void findByIdThrowsExceptionWhenCategoryDoesNotExist() {
    when(categoryRepository.findById("1")).thenReturn(Optional.empty());

    NotFoundException ex =
        assertThrows(NotFoundException.class, () -> categoryService.findById("1"));

    assertEquals("Cannot find category with id = 1", ex.getMessage());
    verify(categoryRepository, times(1)).findById("1");

  }

  @Test
  void findCategoryByName() {
    when(categoryRepository.findByName("Thermodynamics"))
        .thenReturn(Optional.of(new Category("1", "Thermodynamics")));

    assertEquals(new Category("1", "Thermodynamics"), categoryService.findByName("Thermodynamics"));
    verify(categoryRepository, times(1)).findByName("Thermodynamics");
  }

  @Test
  void findByNameThrowsExceptionWhenNameIsNull() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> categoryService.findByName(null));
    assertEquals("Name must not be null or empty", ex.getMessage());
  }

  @Test
  void findByNameThrowsExceptionWhenNameIsEmpty() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> categoryService.findByName(""));
    assertEquals("Name must not be null or empty", ex.getMessage());
  }

  @Test
  void findByNameThrowsExceptionWhenNameIsWhitespace() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> categoryService.findByName("   "));
    assertEquals("Name must not be null or empty", ex.getMessage());
  }

  @Test
  void findByNameThrowsExceptionWhenCategoryDoesNotExist() {
    when(categoryRepository.findByName("Thermodynamics")).thenReturn(Optional.empty());

    NotFoundException ex =
        assertThrows(NotFoundException.class, () -> categoryService.findByName("Thermodynamics"));

    assertEquals("Cannot find category with name = Thermodynamics", ex.getMessage());
    verify(categoryRepository, times(1)).findByName("Thermodynamics");
  }


}
