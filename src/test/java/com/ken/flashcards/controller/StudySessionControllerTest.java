package com.ken.flashcards.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ken.flashcards.model.StudySession;
import com.ken.flashcards.service.StudySessionService;

@WebMvcTest(StudySessionController.class)
public class StudySessionControllerTest extends ControllerTestBase {

  @Value("${spring.servlet.path.study-sessions}")
  private String studySessionsPath;

  @MockitoBean
  private StudySessionService studySessionService;

  @Autowired
  MockMvc mockMvc;

  private StudySession studySession;

  private final String expectedStudySessionId = "009";
  private final String expectedCategoryId = "solar-system-009";
  private final String expectedStudySessionName = "Solar System";

  @BeforeEach
  void setup() {
    this.studySession =
        new StudySession(expectedStudySessionId, expectedCategoryId, expectedStudySessionName);
  }
}
