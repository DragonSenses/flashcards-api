package com.ken.flashcards.mapper;

import com.ken.flashcards.dto.CategoryRequest;
import com.ken.flashcards.model.Category;

public interface CategoryMapper {

  Category categoryFrom(CategoryRequest categoryRequest);

}
