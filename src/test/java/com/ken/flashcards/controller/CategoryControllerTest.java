package com.ken.flashcards.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

}
