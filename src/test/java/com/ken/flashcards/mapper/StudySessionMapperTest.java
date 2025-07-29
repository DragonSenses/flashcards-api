package com.ken.flashcards.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ken.flashcards.dto.StudySessionRequest;
import com.ken.flashcards.model.StudySession;

@ExtendWith(MockitoExtension.class)
public class StudySessionMapperTest {

  @Mock
  IdGenerator idGenerator;

  @InjectMocks
  private StudySessionMapperImpl studySessionMapper;

  @Test
  void testStudySessionFrom() {
    String categoryId = "99";
    StudySessionRequest request =
        new StudySessionRequest(categoryId, "Introduction to Quantum Mechanics");

    when(idGenerator.generateId()).thenReturn("ssn-007");
    StudySession studySession = studySessionMapper.studySessionFrom(request);

    assertEquals("ssn-007", studySession.getId());
    assertEquals("99", studySession.getCategoryId());
    assertEquals("Introduction to Quantum Mechanics", studySession.getName());
    verify(idGenerator, times(1)).generateId();
  }

}
