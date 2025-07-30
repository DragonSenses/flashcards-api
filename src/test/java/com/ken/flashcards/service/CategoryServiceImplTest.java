package com.ken.flashcards.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.ken.flashcards.exception.ConflictException;
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

    assertEquals("Category with id '1' not found", ex.getMessage());
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

    assertEquals("Category with name 'Thermodynamics' not found", ex.getMessage());
    verify(categoryRepository, times(1)).findByName("Thermodynamics");
  }

  @Test
  void createCategory() {
    when(categoryRepository.existsByName(request.getName())).thenReturn(false);
    when(categoryMapper.categoryFrom(request)).thenReturn(category);
    when(categoryRepository.save(category)).thenReturn(category);

    assertEquals(categoryService.createCategory(request), category);
    verify(categoryRepository, times(1)).existsByName(request.getName());
    verify(categoryMapper, times(1)).categoryFrom(request);
    verify(categoryRepository, times(1)).save(category);
  }

  @Test
  void createCategoryWithDuplicateNameThrowsConflictException() {
    when(categoryRepository.existsByName("Astronomy")).thenReturn(true);

    ConflictException ex = assertThrows(ConflictException.class,
        () -> categoryService.createCategory(new CategoryRequest("Astronomy")));

    assertEquals("Category with name 'Astronomy' already exists", ex.getMessage());
    verify(categoryRepository, times(1)).existsByName("Astronomy");
  }


  @Test
  void deletesCategoryWhenIdExists() {
    when(categoryRepository.existsById("1")).thenReturn(true);

    categoryService.deleteById("1");
    verify(categoryRepository, times(1)).deleteById("1");
  }

  @Test
  void throwExceptionWhenDeletingNonExistentCategory() {
    when(categoryRepository.existsById("1")).thenReturn(false);

    NotFoundException ex =
        assertThrows(NotFoundException.class, () -> categoryService.deleteById("1"));

    assertEquals("Category with id '1' not found", ex.getMessage());
    verify(categoryRepository, times(1)).existsById("1");
  }

  @Test
  void savesCategorySuccessfully() {
    when(categoryRepository.save(category)).thenReturn(category);
    assertEquals(category, categoryService.save(category));
    verify(categoryRepository, times(1)).save(category);
  }

  @Test
  void returnsTrueWhenCategoryExistsById() {
    when(categoryRepository.existsById("1")).thenReturn(true);

    assertTrue(categoryService.existsById("1"));
    verify(categoryRepository, times(1)).existsById("1");
  }

  @Test
  void throwsNotFoundExceptionWhenCategoryIdDoesNotExist() {
    when(categoryRepository.existsById("1")).thenReturn(false);

    Throwable ex =
        assertThrows(NotFoundException.class, () -> categoryService.assertExistsById("1"));

    assertEquals("Category with id '1' not found", ex.getMessage());
  }

  @Test
  void throwsNotFoundExceptionWhenCategoryNameDoesNotExist() {
    when(categoryRepository.findByName("English")).thenReturn(Optional.empty());

    Throwable ex = assertThrows(NotFoundException.class,
        () -> categoryService.idFromCategoryWithName("English"));

    assertEquals("Category with name 'English' not found", ex.getMessage());
    verify(categoryRepository, times(1)).findByName("English");
  }

  @Test
  void returnsIdWhenCategoryNameExists() {
    when(categoryRepository.findByName("History"))
        .thenReturn(Optional.of(new Category("10", "History")));

    String id = categoryService.idFromCategoryWithName("History");

    assertEquals("10", id);
    verify(categoryRepository, times(1)).findByName("History");
  }

}
