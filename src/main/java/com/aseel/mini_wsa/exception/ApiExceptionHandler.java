package com.aseel.mini_wsa.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.aseel.mini_wsa.dto.ValidationError;


@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InvalidIngestionRequestException.class)
    public ResponseEntity<ValidationError> handleInvalidInput(InvalidIngestionRequestException exception) {
        return ResponseEntity.badRequest().body(new ValidationError(exception.getMessage(), "input"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationError> handleUnreadableBody(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(new ValidationError("Malformed JSON or missing request body.", "input"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleInvalidBody(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        if (fieldError == null) {
            return ResponseEntity.badRequest().body(new ValidationError("Invalid request body.", "input"));
        }
        return ResponseEntity.badRequest().body(
                new ValidationError(fieldError.getDefaultMessage(), fieldError.getField()));
    }

    @ExceptionHandler(InvalidQueryException.class)
    public ResponseEntity<ValidationError> handleInvalidQuery(
            InvalidQueryException exception) {
        return ResponseEntity.badRequest().body(
            new ValidationError(exception.getMessage(), "query")
        );
    }
}
