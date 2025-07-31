package com.ken.flashcards.service;

import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

  private final String nonexistentSessionId = "session-neptune-999";
  private final String nonexistentSessionName = "Advanced Conjuration";
  private final String existingSessionName = "Engineering";


  private static final String CANNOT_FIND_BY_ID = "Study session with ID '%s' not found";
  private static final String CANNOT_FIND_BY_NAME = "Study session with name '%s' not found";
  private static final String CANNOT_FIND_CATEGORY_BY_ID = "Study session with category ID '%s' not found";

  @BeforeEach
  void init() {
    String studySessionId = expectedSessionId;
    String associatedCategoryId = expectedCategoryId;
    String sessionName = expectedSessionName;

    this.studySession = new StudySession(studySessionId, associatedCategoryId, sessionName);
    this.studySessions = List.of(studySession);
  }

  // findAll()
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

  // findById()
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

  // createStudySession()
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

  // createStudySession()
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

  // save()
  // Verifies that a StudySession is saved and returned correctly
  @Test
  void shouldSaveStudySessionToRepository() {
    when(studySessionRepository.save(studySession)).thenReturn(studySession);

    assertEquals(studySession, studySessionService.save(studySession));
    verify(studySessionRepository, times(1)).save(studySession);
  }

  // existsById()
  // Confirms existence check behavior for valid StudySession IDs
  @Test
  void shouldReturnTrueWhenStudySessionExistsById() {
    when(studySessionRepository.existsById(expectedSessionId)).thenReturn(true);

    assertTrue(studySessionService.existsById(expectedSessionId));
    verify(studySessionRepository, times(1)).existsById(expectedSessionId);
  }

  // existsById()
  // Confirms existence check behavior for invalid StudySession IDs
  @Test
  void shouldReturnFalseWhenStudySessionDoesNotExistById() {
    when(studySessionRepository.existsById(nonexistentSessionId)).thenReturn(false);

    assertFalse(studySessionService.existsById(nonexistentSessionId));
    verify(studySessionRepository, times(1)).existsById(nonexistentSessionId);
  }

  // deleteById()
  // Verifies deletion logic for an existing StudySession
  @Test
  void shouldDeleteStudySessionByIdIfExists() {
    when(studySessionRepository.existsById(expectedSessionId)).thenReturn(true);

    studySessionService.deleteById(expectedSessionId);
    verify(studySessionRepository, times(1)).deleteById(expectedSessionId);
  }

  // deleteById()
  // Throws NotFoundException when attempting to delete non-existent StudySession
  @Test
  void shouldThrowExceptionWhenDeletingMissingStudySessionById() {
    when(studySessionRepository.existsById(nonexistentSessionId)).thenReturn(false);

    NotFoundException ex = assertThrows(NotFoundException.class,
        () -> studySessionService.deleteById(nonexistentSessionId));
    
    assertEquals(format(CANNOT_FIND_BY_ID, nonexistentSessionId), ex.getMessage());
    verify(studySessionRepository, times(1)).existsById(nonexistentSessionId);
  }

  // assertExistsById()
  // Throws exception when StudySession does not exist
  @Test
  void shouldThrowExceptionIfStudySessionDoesNotExistById() {
    when(studySessionRepository.existsById(nonexistentSessionId)).thenReturn(false);

    NotFoundException ex = assertThrows(NotFoundException.class,
        () -> studySessionService.assertExistsById(nonexistentSessionId));
    assertEquals(format(CANNOT_FIND_BY_ID, nonexistentSessionId), ex.getMessage());
  }

  // assertExistsById()
  // Confirms no exception is thrown when StudySession exists
  @Test
  void shouldNotThrowExceptionIfStudySessionExistsById() {
    when(studySessionRepository.existsById(expectedSessionId)).thenReturn(true);

    assertDoesNotThrow(() -> studySessionService.assertExistsById(expectedSessionId));
  }

  // idFromStudySessionWithName()
  // Retrieves the ID from a StudySession given its name
  @Test
  void shouldReturnIdFromStudySessionWhenNameExists() {
    when(studySessionRepository.findByName(existingSessionName))
        .thenReturn(Optional.of(new StudySession("99", "9", existingSessionName)));

    String id = studySessionService.idFromStudySessionWithName(existingSessionName);
    assertEquals("99", id);
    verify(studySessionRepository, times(1)).findByName(existingSessionName);
  }

  // idFromStudySessionWithName()
  // Throws NotFoundException when StudySession with name is not found
  @Test
  void shouldThrowExceptionWhenStudySessionNameDoesNotExist() {
    when(studySessionRepository.findByName(nonexistentSessionName)).thenReturn(Optional.empty());

    NotFoundException ex = assertThrows(NotFoundException.class,
        () -> studySessionService.idFromStudySessionWithName(nonexistentSessionName));
    
    assertEquals(format(CANNOT_FIND_BY_NAME, nonexistentSessionName), ex.getMessage());
  }
}
