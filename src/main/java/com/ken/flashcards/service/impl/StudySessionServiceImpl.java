package com.ken.flashcards.service.impl;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ken.flashcards.dto.StudySessionRequest;
import com.ken.flashcards.exception.NotFoundException;
import com.ken.flashcards.mapper.StudySessionMapper;
import com.ken.flashcards.model.StudySession;
import com.ken.flashcards.repository.StudySessionRepository;
import com.ken.flashcards.service.CategoryService;
import com.ken.flashcards.service.StudySessionService;
import com.ken.flashcards.service.ValidatingService;

@Service
@Transactional
public class StudySessionServiceImpl extends ValidatingService implements StudySessionService {

  private final StudySessionRepository studySessionRepository;
  private final CategoryService categoryService;
  private final StudySessionMapper studySessionMapper;

  @Autowired
  public StudySessionServiceImpl(StudySessionRepository studySessionRepository,
      CategoryService categoryService, StudySessionMapper studySessionMapper) {
    this.studySessionRepository = studySessionRepository;
    this.categoryService = categoryService;
    this.studySessionMapper = studySessionMapper;
  }

  @Override
  public Iterable<StudySession> findAll() {
    return studySessionRepository.findAll();
  }

  @Override
  public StudySession findById(String id) {
    return studySessionRepository.findById(id).orElseThrow(
        () -> new NotFoundException(format("Cannot find study session with id = %s", id)));
  }

  @Override
  public StudySession createStudySession(StudySessionRequest request) {
    validate(request);
    StudySession studySession = studySessionFrom(request);
    return studySessionRepository.save(studySession);
  }

  @Override
  public StudySession save(StudySession studySession) {
    validate(studySession);
    return studySessionRepository.save(studySession);
  }

  @Override
  public boolean existsById(String id) {
    return studySessionRepository.existsById(id);
  }

  @Override
  public void deleteById(String id) {
    assertExistsById(id);
    studySessionRepository.deleteById(id);
  }

  @Override
  public void assertExistsById(String id) {
    if (!existsById(id)) {
      throw new NotFoundException(format("Cannot find study session with id = %s", id));
    }
  }

  @Override
  public String idFromStudySessionWithName(String name) {
    return findByName(name).getId();
  }

  @Override
  public Iterable<StudySession> findAllByCategoryId(String categoryId) {
    categoryService.assertExistsById(categoryId);
    return studySessionRepository.findAllByCategoryId(categoryId);
  }

  private StudySession findByName(String name) {
    return studySessionRepository.findByName(name).orElseThrow(
        () -> new NotFoundException(format("Cannot find study session with name = %s", name)));
  }

  private void validate(StudySession studySession) {
    assertNotNull(studySession);
    categoryService.assertExistsById(studySession.getCategoryId());
  }

  private void validate(StudySessionRequest request) {
    categoryService.assertExistsById(request.getCategoryId());
  }

  private StudySession studySessionFrom(StudySessionRequest request) {
    return studySessionMapper.studySessionFrom(request);
  }

}
