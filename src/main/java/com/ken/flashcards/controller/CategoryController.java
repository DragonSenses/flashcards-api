package com.ken.flashcards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ken.flashcards.dto.CategoryRequest;
import com.ken.flashcards.model.Category;
import com.ken.flashcards.service.CategoryService;
import com.ken.flashcards.error.ResponseHandler;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController implements ResponseHandler {

  private final CategoryService categoryService;

  @Autowired
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping
  public ResponseEntity<Collection<Category>> findAll() {
    return response(categoryService.findAll(), org.springframework.http.HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Category> findById(@PathVariable String id) {
    return response(categoryService.findById(id), org.springframework.http.HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<Category> create(@RequestBody CategoryRequest request) {
    Category category = categoryService.createCategory(request);
    return created(category);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    categoryService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

}