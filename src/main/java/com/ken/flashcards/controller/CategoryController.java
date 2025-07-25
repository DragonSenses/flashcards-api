package com.ken.flashcards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.ok;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ken.flashcards.dto.CategoryRequest;
import com.ken.flashcards.error.ErrorResponse;
import com.ken.flashcards.error.ResponseHandler;
import com.ken.flashcards.model.Category;
import com.ken.flashcards.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Category", description = "Category resource operations")
public class CategoryController implements ResponseHandler {

  private final CategoryService categoryService;

  @Autowired
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @Operation(summary = "Get all categories")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved categories",
      content = @Content(mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = Category.class))))
  @GetMapping
  public ResponseEntity<Iterable<Category>> findAll() {
    return ok(categoryService.findAll());
  }

  @Operation(summary = "Get a category by ID")
  @ApiResponse(responseCode = "200", description = "Found category",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Category.class)))
  @ApiResponse(responseCode = "404", description = "Category not found",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class)))
  @GetMapping("/{id}")
  public ResponseEntity<Category> findById(@PathVariable String id) {
    return response(categoryService.findById(id), org.springframework.http.HttpStatus.OK);
  }

  @Operation(summary = "GET a category by its name")
  @ApiResponse(responseCode = "200", description = "Category found",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Category.class)))
  @ApiResponse(responseCode = "404", description = "Category not found",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class)))
  @GetMapping("/details")
  public ResponseEntity<Category> findByName(@RequestParam String name) {
    return ok(categoryService.findByName(name));
  }

  @Operation(summary = "Create a category")
  @ApiResponse(responseCode = "201", description = "Category created",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Category.class)))
  @PostMapping
  public ResponseEntity<Category> create(@RequestBody CategoryRequest request) {
    return created(categoryService.createCategory(request));
  }

  @Operation(summary = "Delete a category by ID")
  @ApiResponse(responseCode = "204", description = "Category deleted")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    categoryService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping
  @Operation(summary = "Create a category")
  @ApiResponse(responseCode = "201", description = "Category created",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Category.class)))
  @ApiResponse(responseCode = "400", description = "Invalid request body",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "409", description = "Category already exists",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryRequest request) {
    return created(categoryService.createCategory(request));
  }

  @PutMapping
  @Operation(summary = "Update a category")
  @ApiResponse(responseCode = "200", description = "Category updated",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Category.class)))
  @ApiResponse(responseCode = "201", description = "New category created",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = Category.class)))
  @ApiResponse(responseCode = "400", description = "Invalid request body",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "409", description = "Duplicate category name",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<Category> update(@Valid @RequestBody Category category) {
    return existsById(category.getId()) ? ok(save(category)) : created(save(category));
  }

  private boolean existsById(String id) {
    return categoryService.existsById(id);
  }

  private Category save(Category category) {
    return categoryService.save(category);
  }
}
