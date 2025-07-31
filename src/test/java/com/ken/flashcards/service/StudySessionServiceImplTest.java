package com.ken.flashcards.service;

import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ken.flashcards.dto.StudySessionRequest;
import com.ken.flashcards.exception.NotFoundException;
import com.ken.flashcards.mapper.StudySessionMapper;
import com.ken.flashcards.model.StudySession;
import com.ken.flashcards.repository.StudySessionRepository;
import com.ken.flashcards.service.impl.StudySessionServiceImpl;

@ExtendWith(MockitoExtension.class)
public class StudySessionServiceImplTest {

  @Mock
  private StudySessionRepository studySessionRepository;

  @Mock
  private CategoryService categoryService;

  @Mock
  private StudySessionMapper studySessionMapper;

  @InjectMocks
  private StudySessionServiceImpl studySessionService;

  private StudySession studySession;

  private List<StudySession> studySessions;

  private final String expectedSessionId = "session-astro-001";
  private final String expectedCategoryId = "category-space-science";
  private final String expectedSessionName = "Introduction to Astronomy";

  private static final String CANNOT_FIND_BY_ID = "Study session with ID '%s' not found";
  private static final String CANNOT_FIND_BY_NAME = "Study session with name '%s' not found";
  private static final String CANNOT_FIND_CATEGORY_BY_ID =
      "Study session with cateogry ID '%s' not found";

  @BeforeEach
  void init() {
    String studySessionId = expectedSessionId;
    String associatedCategoryId = expectedCategoryId;
    String sessionName = expectedSessionName;

    this.studySession = new StudySession(studySessionId, associatedCategoryId, sessionName);
    this.studySessions = List.of(studySession);
  }

  // findALl()
  // Verifies that all stored study sessions are returned from the repository
  @Test
  void shouldReturnAllStudySessionsFromRepository() {
    when(studySessionRepository.findAll()).thenReturn(studySessions);

    assertEquals(studySessions, studySessionService.findAll());
    verify(studySessionRepository, times(1)).findAll();
  }

  // findById()
  // Ensures a valid study session ID returns the correct session
  @Test
  void shouldReturnStudySessionByIdWhenPresent() {
    when(studySessionRepository.findById(expectedSessionId)).thenReturn(Optional.of(studySession));

    assertEquals(studySession, studySessionService.findById(expectedSessionId));
    verify(studySessionRepository, times(1)).findById(expectedSessionId);
  }

  // Confirms an exception is thrown if the study session ID doesn't exist
  @Test
  void shouldThrowNotFoundExceptionWhenStudySessionDoesNotExist() {
    when(studySessionRepository.findById(expectedSessionId)).thenReturn(Optional.empty());

    NotFoundException ex = assertThrows(NotFoundException.class,
        () -> studySessionService.findById(expectedSessionId));

    assertEquals(format(CANNOT_FIND_BY_ID, expectedSessionId), ex.getMessage());
    verify(studySessionRepository, times(1)).findById(expectedSessionId);
  }

  // findAllByCategoryId()
  // Checks that study sessions associated with a specific category ID are returned
  @Test
  void shouldReturnStudySessionsByCategoryId() {
    when(studySessionRepository.findAllByCategoryId(expectedCategoryId))
        .thenReturn(Set.of(studySession));

    assertEquals(Set.of(studySession), studySessionService.findAllByCategoryId(expectedCategoryId));
    verify(studySessionRepository, times(1)).findAllByCategoryId(expectedCategoryId);
  }

  // Verifies successful creation of a StudySession from a valid request
  @Test
  void shouldCreateStudySessionWithValidCategory() {
    StudySessionRequest request = new StudySessionRequest(expectedCategoryId, expectedSessionName);

    when(studySessionMapper.studySessionFrom(request)).thenReturn(studySession);
    when(studySessionRepository.save(studySession)).thenReturn(studySession);

    assertEquals(studySession, studySessionService.createStudySession(request));
    verify(categoryService, times(1)).assertExistsById(expectedCategoryId);
    verify(studySessionMapper, times(1)).studySessionFrom(request);
    verify(studySessionRepository, times(1)).save(studySession);
  }

  // Ensures NotFoundException is thrown when creating a StudySession with a missing category
  @Test
  void shouldThrowExceptionWhenCreatingStudySessionWithInvalidCategory() {
    doThrow(new NotFoundException(format(CANNOT_FIND_CATEGORY_BY_ID, expectedCategoryId)))
        .when(categoryService).assertExistsById(expectedCategoryId);

    StudySessionRequest request = new StudySessionRequest(expectedCategoryId, expectedSessionName);

    NotFoundException ex = assertThrows(NotFoundException.class,
        () -> studySessionService.createStudySession(request));

    assertEquals(format(CANNOT_FIND_CATEGORY_BY_ID, expectedCategoryId), ex.getMessage());
    verify(categoryService, times(1)).assertExistsById(expectedCategoryId);
  }

}
