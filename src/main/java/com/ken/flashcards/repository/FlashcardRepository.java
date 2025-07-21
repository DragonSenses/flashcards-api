package com.ken.flashcards.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ken.flashcards.model.Flashcard;

public interface FlashcardRepository extends JpaRepository<Flashcard, String> {

  Iterable<Flashcard> findAllByStudySessionId(String studySessionId);

}
