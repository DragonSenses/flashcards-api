package com.ken.flashcards.service;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

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

  @BeforeEach
  void init() {
    String studySessionId = expectedSessionId;
    String associatedCategoryId = expectedCategoryId;
    String sessionName = expectedSessionName;

    this.studySession = new StudySession(studySessionId, associatedCategoryId, sessionName);
    this.studySessions = List.of(studySession);
  }

  @Test
  void shouldReturnAllStudySessionsFromRepository() {
    when(studySessionRepository.findAll()).thenReturn(studySessions);

    assertEquals(studySessions, studySessionService.findAll());
    verify(studySessionRepository, times(1)).findAll();
  }

  @Test
  void shouldReturnStudySessionByIdWhenPresent() {
    when(studySessionRepository.findById(expectedSessionId)).thenReturn(Optional.of(studySession));

    assertEquals(studySession, studySessionService.findById(expectedSessionId));
    verify(studySessionRepository, times(1)).findById(expectedSessionId);
  }

  @Test
  void shouldThrowNotFoundExceptionWhenStudySessionDoesNotExist() {
    when(studySessionRepository.findById(expectedSessionId)).thenReturn(Optional.empty());

    NotFoundException ex = assertThrows(NotFoundException.class,
        () -> studySessionService.findById(expectedSessionId));

    assertEquals("StudySession with id '" + expectedSessionId + "' not found", ex.getMessage());
    verify(studySessionRepository, times(1)).findById(expectedSessionId);
  }


  @Test
  void shouldReturnStudySessionsByCategoryId() {
    when(studySessionRepository.findAllByCategoryId(expectedCategoryId))
        .thenReturn(Set.of(studySession));

    assertEquals(Set.of(studySession), studySessionService.findAllByCategoryId(expectedCategoryId));
    verify(studySessionRepository, times(1)).findAllByCategoryId(expectedCategoryId);
  }

}
