package com.ken.flashcards.error;

import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.CREATED;
import org.springframework.http.ResponseEntity;

public interface ResponseHandler {

  default <T> ResponseEntity<T> created(T content) {
    return response(content, CREATED);
  }

  default <T> ResponseEntity<T> response(T content, HttpStatus status) {
    return new ResponseEntity<>(content, status);
  }

}
