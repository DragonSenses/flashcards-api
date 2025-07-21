package com.ken.flashcards.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ken.flashcards.model.StudySession;

public interface StudySessionRepository extends JpaRepository<StudySession, String> {

  Optional<StudySession> findByName(String name);

  Iterable<StudySession> findAllByCategoryId(String id);

}
