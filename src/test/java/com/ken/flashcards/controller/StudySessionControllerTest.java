package com.ken.flashcards.controller;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

  @Test
  @DisplayName("GET /api/v1/sessions returns all study sessions")
  void returnsAllStudySessionsSuccessfully() throws Exception {
    when(studySessionService.findAll()).thenReturn(Set.of(studySession));

    mockMvc.perform(get(studySessionsPath).contentType(APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(content().json(serialize(Set.of(studySession))));
  }

}
