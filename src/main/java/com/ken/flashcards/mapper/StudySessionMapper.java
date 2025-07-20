package com.ken.flashcards.mapper;

import com.ken.flashcards.dto.StudySessionRequest;
import com.ken.flashcards.model.StudySession;

public interface StudySessionMapper {

  StudySession studySessionFrom(StudySessionRequest request);

}
