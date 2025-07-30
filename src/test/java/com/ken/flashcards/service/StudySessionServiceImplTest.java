package com.ken.flashcards.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}
