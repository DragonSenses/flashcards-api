package com.ken.flashcards.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ken.flashcards.dto.CategoryRequest;
import com.ken.flashcards.model.Category;

@ExtendWith(MockitoExtension.class)
public class CategoryMapperTest {

  @Mock
  private IdGenerator idGenerator;

  @InjectMocks
  private CategoryMapperImpl categoryMapper;

  @Test
  void testCategoryFrom() {
    CategoryRequest request = new CategoryRequest("Science");

    when(idGenerator.generateId()).thenReturn("1");
    Category category = categoryMapper.categoryFrom(request);

    assertEquals("1", category.getId());
    assertEquals("Science", category.getName());
    verify(idGenerator, times(1)).generateId();
  }
}
