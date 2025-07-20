package com.ken.flashcards.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ken.flashcards.dto.StudySessionRequest;
import com.ken.flashcards.model.StudySession;

@Component
public class StudySessionMapperImpl implements StudySessionMapper {

  private final IdGenerator idGenerator;

  @Autowired
  public StudySessionMapperImpl(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  public StudySession studySessionFrom(StudySessionRequest request) {
    return new StudySession(
      idGenerator.generateId(),
      request.getCategoryId(),
      request.getName()
    );
  }
}