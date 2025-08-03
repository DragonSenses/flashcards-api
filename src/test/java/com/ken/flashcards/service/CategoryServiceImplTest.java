package com.ken.flashcards.service;

import static java.lang.String.format;
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

import static com.ken.flashcards.constants.ExceptionMessages.CANNOT_FIND_CATEGORY_BY_ID;
import static com.ken.flashcards.constants.ExceptionMessages.CANNOT_FIND_CATEGORY_BY_NAME;
import static com.ken.flashcards.constants.ExceptionMessages.CATEGORY_NAME_ALREADY_EXISTS;
import static com.ken.flashcards.constants.ExceptionMessages.FIELD_MUST_NOT_BE_NULL_OR_EMPTY;
import com.ken.flashcards.dto.CategoryRequest;
import com.ken.flashcards.exception.BadRequestException;
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

  private static final String CATEGORY_ID = "Category ID";
  private static final String CATEGORY_NAME = "Category name";

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
    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> categoryService.findById(null));
    assertEquals(format(FIELD_MUST_NOT_BE_NULL_OR_EMPTY, CATEGORY_ID), ex.getMessage());
  }

  @Test
  void findByIdThrowsExceptionWhenIdIsEmpty() {
    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> categoryService.findById(""));
    assertEquals(format(FIELD_MUST_NOT_BE_NULL_OR_EMPTY, CATEGORY_ID), ex.getMessage());
  }

  @Test
  void findByIdThrowsExceptionWhenIdIsWhitespace() {
    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> categoryService.findById("   "));
    assertEquals(format(FIELD_MUST_NOT_BE_NULL_OR_EMPTY, CATEGORY_ID), ex.getMessage());
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

    assertEquals(format(CANNOT_FIND_CATEGORY_BY_ID, "1"), ex.getMessage());
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
    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> categoryService.findByName(null));
    assertEquals(format(FIELD_MUST_NOT_BE_NULL_OR_EMPTY, CATEGORY_NAME), ex.getMessage());
  }

  @Test
  void findByNameThrowsExceptionWhenNameIsEmpty() {
    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> categoryService.findByName(""));
    assertEquals(format(FIELD_MUST_NOT_BE_NULL_OR_EMPTY, CATEGORY_NAME), ex.getMessage());
  }

  @Test
  void findByNameThrowsExceptionWhenNameIsWhitespace() {
    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> categoryService.findByName("   "));
    assertEquals(format(FIELD_MUST_NOT_BE_NULL_OR_EMPTY, CATEGORY_NAME), ex.getMessage());
  }

  @Test
  void findByNameThrowsExceptionWhenCategoryDoesNotExist() {
    when(categoryRepository.findByName("Thermodynamics")).thenReturn(Optional.empty());

    NotFoundException ex =
        assertThrows(NotFoundException.class, () -> categoryService.findByName("Thermodynamics"));

    assertEquals(format(CANNOT_FIND_CATEGORY_BY_NAME, "Thermodynamics"), ex.getMessage());
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

    assertEquals(format(CATEGORY_NAME_ALREADY_EXISTS, "Astronomy"), ex.getMessage());
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

    assertEquals(format(CANNOT_FIND_CATEGORY_BY_ID, "1"), ex.getMessage());
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

    NotFoundException ex =
        assertThrows(NotFoundException.class, () -> categoryService.assertExistsById("1"));

    assertEquals(format(CANNOT_FIND_CATEGORY_BY_ID, "1"), ex.getMessage());
  }

  @Test
  void throwsNotFoundExceptionWhenCategoryNameDoesNotExist() {
    when(categoryRepository.findByName("English")).thenReturn(Optional.empty());

    NotFoundException ex = assertThrows(NotFoundException.class,
        () -> categoryService.idFromCategoryWithName("English"));

    assertEquals(format(CANNOT_FIND_CATEGORY_BY_NAME, "English"), ex.getMessage());
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
