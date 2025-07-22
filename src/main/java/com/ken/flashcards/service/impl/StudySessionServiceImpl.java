package com.ken.flashcards.service.impl;

import com.ken.flashcards.dto.StudySessionRequest;
import com.ken.flashcards.exception.NotFoundException;
import com.ken.flashcards.mapper.StudySessionMapper;
import com.ken.flashcards.model.StudySession;
import com.ken.flashcards.repository.StudySessionRepository;
import com.ken.flashcards.service.CategoryService;
import com.ken.flashcards.service.StudySessionService;

import static java.lang.String.format;

public class StudySessionServiceImpl implements StudySessionService {
  private final StudySessionRepository studySessionRepository;
  private final StudySessionMapper studySessionMapper;

  public StudySessionServiceImpl(StudySessionRepository studySessionRepository,
      CategoryService categoryService, StudySessionMapper studySessionMapper) {
    this.studySessionRepository = studySessionRepository;
    this.studySessionMapper = studySessionMapper;
  }

  @Override
  public StudySession findById(String id) {
    return studySessionRepository.findById(id).orElseThrow(
        () -> new NotFoundException(format("Cannot find study session with id = %s", id)));
  }

  @Override
  public StudySession createStudySession(StudySessionRequest request) {
    StudySession studySession = studySessionFrom(request);
    return studySessionRepository.save(studySession);
  }

  private StudySession studySessionFrom(StudySessionRequest request) {
      return studySessionMapper.studySessionFrom(request);
  }
}
