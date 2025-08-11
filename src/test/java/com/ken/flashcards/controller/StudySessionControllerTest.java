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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ken.flashcards.constants.ExceptionMessages;
import com.ken.flashcards.dto.StudySessionRequest;
import com.ken.flashcards.exception.ConflictException;
import com.ken.flashcards.exception.NotFoundException;
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
  void shouldReturnAllStudySessions() throws Exception {
    when(studySessionService.findAll()).thenReturn(Set.of(studySession));

    mockMvc.perform(get(studySessionsPath).contentType(APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(content().json(serialize(Set.of(studySession))));
  }

  @Test
  @DisplayName("GET /api/v1/sessions/{id} returns study session by ID")
  void shouldReturnStudySessionById() throws Exception {
    when(studySessionService.findById(expectedStudySessionId)).thenReturn(studySession);

    mockMvc
        .perform(
            get(studySessionsPath + "/" + expectedStudySessionId).contentType(APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(content().json(serialize(studySession)));
  }

  @Test
  @DisplayName("GET /api/v1/sessions/{id} returns 404 when study session is not found")
  void shouldReturnNotFoundWhenStudySessionIsInvalid() throws Exception {
    String nonexistentStudySessionId = "nonexistent-session-id-123";

    String errorMessage =
        String.format(ExceptionMessages.CANNOT_FIND_STUDY_SESSION_BY_ID, nonexistentStudySessionId);

    when(studySessionService.findById(nonexistentStudySessionId))
        .thenThrow(new NotFoundException(errorMessage));

    mockMvc
        .perform(
            get(studySessionsPath + "/" + nonexistentStudySessionId).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\"error\":\"" + errorMessage + "\"}"));
  }

  @Test
  @DisplayName("GET /api/v1/sessions/details?categoryId returns 200 with sessions for category")
  void shouldReturnStudySessionsByCategoryId() throws Exception {
    when(studySessionService.findAllByCategoryId(expectedCategoryId))
        .thenReturn(Set.of(studySession));

    mockMvc
        .perform(get(studySessionsPath + "/details").param("categoryId", expectedCategoryId)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(content().json(serialize(Set.of(studySession))));
  }

  @Test
  @DisplayName("POST /api/v1/sessions creates study session when it does not exist")
  void shouldCreateStudySessionViaPostWhenIdIsNew() throws Exception {
    StudySessionRequest request =
        new StudySessionRequest(expectedCategoryId, expectedStudySessionName);

    when(studySessionService.createStudySession(request)).thenReturn(studySession);

    mockMvc
        .perform(post(studySessionsPath).contentType(APPLICATION_JSON).content(serialize(request)))
        .andExpect(status().isCreated()).andExpect(content().json(serialize(studySession)));
  }

  @Test
  @DisplayName("POST /api/v1/sessions returns 409 when study session name already exists")
  void shouldReturnConflictWhenStudySessionNameAlreadyExists() throws Exception {
    String sameStudySessionName = expectedStudySessionName;
    String differentCategoryId = "2";
    String errorMessage =
        String.format(ExceptionMessages.STUDY_SESSION_NAME_ALREADY_EXISTS, sameStudySessionName);

    StudySessionRequest request =
        new StudySessionRequest(differentCategoryId, sameStudySessionName);

    when(studySessionService.createStudySession(request))
        .thenThrow(new ConflictException(errorMessage));

    mockMvc
        .perform(post(studySessionsPath).contentType(APPLICATION_JSON).content(serialize(request)))
        .andExpect(status().isConflict())
        .andExpect(content().json("{\"error\":\"" + errorMessage + "\"}"));
  }

  @Test
  @DisplayName("PUT /api/v1/sessions returns 400 when request body is empty")
  void shouldReturnBadRequestViaPutWhenRequestBodyIsEmpty() throws Exception {
    mockMvc.perform(put(studySessionsPath).contentType(APPLICATION_JSON).content(""))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /api/v1/sessions returns 404 when category ID does not exist")
  void shouldReturnNotFoundWhenCategoryIdIsInvalid() throws Exception {
    String nonExistentCategoryId = "32";
    String errorMessage =
        String.format(ExceptionMessages.CANNOT_FIND_CATEGORY_BY_ID, nonExistentCategoryId);

    StudySessionRequest request =
        new StudySessionRequest(expectedStudySessionId, expectedStudySessionName);
    when(studySessionService.createStudySession(request))
        .thenThrow(new NotFoundException(errorMessage));

    mockMvc
        .perform(post(studySessionsPath).contentType(APPLICATION_JSON).content(serialize(request)))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\"error\":\"" + errorMessage + "\"}"));
  }


}
