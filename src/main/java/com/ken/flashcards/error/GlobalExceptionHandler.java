package com.ken.flashcards.error;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ken.flashcards.exception.BadRequestException;
import com.ken.flashcards.exception.ConflictException;
import com.ken.flashcards.exception.NotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler implements ResponseHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    ErrorResponse handle(NotFoundException exception) {
        return responseFrom(exception);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    ErrorResponse handle(BadRequestException exception) {
        return responseFrom(exception);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(CONFLICT)
    @ResponseBody
    ErrorResponse handle(ConflictException exception) {
        return responseFrom(exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    Map<String, List<String>> handle(MethodArgumentNotValidException exception) {
        var errorsExtractor = new ValidationErrorExtractor();
        return errorsExtractor.extractErrorsFrom(exception);
    }

    private ErrorResponse responseFrom(RuntimeException exception) {
        return ErrorResponse.withMessage(exception.getMessage());
    }

}