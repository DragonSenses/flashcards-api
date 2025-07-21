package com.ken.flashcards.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ken.flashcards.model.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {

  Collection<Category> findAllByOrderByNameAsc();

  boolean existsByName(String name);

  Optional<Category> findByName(String name);

}
