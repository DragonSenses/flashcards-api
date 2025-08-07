package com.ken.flashcards.controller;

import static java.lang.String.format;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.ken.flashcards.constants.ExceptionMessages.CANNOT_FIND_CATEGORY_BY_ID;
import static com.ken.flashcards.constants.ExceptionMessages.CANNOT_FIND_CATEGORY_BY_NAME;
import com.ken.flashcards.dto.CategoryRequest;
import com.ken.flashcards.exception.NotFoundException;
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

  // findById(String id)
  // Verifies that GET /categories/{id} returns the correct category with HTTP 200 and expected JSON
  @Test
  void shouldReturnCategoryByIdSuccessfully() throws Exception {
    when(categoryService.findById(expectedCategoryId))
        .thenReturn(new Category(expectedCategoryId, expectedCategoryName));

    mockMvc.perform(get(categoriesPath + "/" + expectedCategoryId).contentType(APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(
            content().json(serialize(new Category(expectedCategoryId, expectedCategoryName))));
  }

  // findById(String id)
  // Should return 404 when attempting to retrieve a non-existent category by ID
  @Test
  void shouldReturn404WhenCategoryIsNotFoundById() throws Exception {
    // Arrange
    String id = "1";
    String errorMessage = format(CANNOT_FIND_CATEGORY_BY_ID, id);
    when(categoryService.findById(id)).thenThrow(new NotFoundException(errorMessage));

    // Act & Assert
    mockMvc.perform(get(categoriesPath + "/" + id).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
        .andExpect(content().json("{\"error\":\"" + errorMessage + "\"}"));
  }

  // findByName(String name)
  // Should return category details when a valid name is provided
  @Test
  void shouldReturnCategoryDetailsWhenFoundByName() throws Exception {
    // Arrange
    when(categoryService.findByName(expectedCategoryName))
        .thenReturn(new Category(expectedCategoryId, expectedCategoryName));

    // Act & Assert
    mockMvc
        .perform(get(categoriesPath + "/details?name=" + expectedCategoryName)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(content()
            .json("{'id':'" + expectedCategoryId + "','name':'" + expectedCategoryName + "'}"));
  }

  // findByName(String name)
  // Should return 404 when attempting to retrieve a non-existent category by name
  @Test
  void shouldReturn404WhenCategoryIsNotFoundByName() throws Exception {
    // Arrange
    String errorMessage = format(CANNOT_FIND_CATEGORY_BY_NAME, expectedCategoryName);
    when(categoryService.findByName(expectedCategoryName))
        .thenThrow(new NotFoundException(errorMessage));

    // Act & Assert
    mockMvc
        .perform(get(categoriesPath + "/details?name=" + expectedCategoryName)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\"error\":\"" + errorMessage + "\"}"));
  }

  // createCategory(CategoryRequest request)
  // Should return 201 Created with category details when a valid request is submitted
  @Test
  @DisplayName("POST /categories - should create category when request is valid")
  void shouldCreateCategoryWhenRequestIsValid() throws Exception {
    CategoryRequest request = new CategoryRequest(expectedCategoryName);
    Category expectedCategory = new Category(expectedCategoryId, expectedCategoryName);
    String expectedResponseBody = """
        {
            "id":"%s",
            "name":"%s"
        }
        """.formatted(expectedCategoryId, expectedCategoryName);

    when(categoryService.createCategory(request)).thenReturn(expectedCategory);

    mockMvc
        .perform(post(categoriesPath).contentType(APPLICATION_JSON)
            .content("{\"name\":\"" + expectedCategoryName + "\"}"))
        .andExpect(status().isCreated()).andExpect(content().json(expectedResponseBody));
  }

  // createCategory(CategoryRequest request)
  // Should return 400 Bad Request when the request body is empty
  @Test
  @DisplayName("POST /categories - should return 400 when request body is empty")
  void shouldReturn400WhenCreateCategoryRequestBodyIsEmpty() throws Exception {
    mockMvc.perform(post(categoriesPath).contentType(APPLICATION_JSON).content(""))
        .andExpect(status().isBadRequest());
  }
}
