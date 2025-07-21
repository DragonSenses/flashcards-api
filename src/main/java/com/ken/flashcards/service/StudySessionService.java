package com.ken.flashcards.service;

import com.ken.flashcards.dto.StudySessionRequest;
import com.ken.flashcards.model.StudySession;

public interface StudySessionService {

  Iterable<StudySession> findAll();

  StudySession findById(String id);

  StudySession createStudySession(StudySessionRequest request);

  StudySession save(StudySession studySession);

  boolean existsById(String id);

  void deleteById(String id);

  void assertExistsById(String id);

  String idFromStudySessionWithName(String name);

  Iterable<StudySession> findAllByCategoryId(String categoryId);
}
