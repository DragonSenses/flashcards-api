package com.ken.flashcards.controller;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ken.flashcards.model.Category;
import com.ken.flashcards.service.CategoryService;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest extends ControllerTestBase {

  @Value("${spring.servlet.path.categories}")
  private String categoriesPath;

  @MockitoBean
  private CategoryService categoryService;

  @Autowired
  MockMvc mockMvc;

  private Category category;

  private final String expectedCategoryId = "category-id-001";
  private final String expectedCategoryName = "Music";

  @BeforeEach()
  void init() {
    this.category = new Category(expectedCategoryId, expectedCategoryName);
  }

  // findAll()
  // Verifies that GET /categories returns all categories with HTTP 200 and correct JSON response
  @Test
  void shouldReturnAllCategoriesSuccessfully() throws Exception {
    when(categoryService.findAll()).thenReturn(Set.of(category));

    mockMvc.perform(get(categoriesPath).contentType(APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(content().json(serialize(Set.of(category))));
  }
}
