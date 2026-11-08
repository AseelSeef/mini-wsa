package com.aseel.mini_wsa.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.anthropic.errors.AnthropicException;
import com.aseel.mini_wsa.dto.ValidationError;


@RestControllerAdvice
public class ApiExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

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

    @ExceptionHandler(AnthropicException.class)
    public ResponseEntity<ValidationError> handleAiFailure(AnthropicException exception) {
        log.error("AI service call failed", exception);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                new ValidationError("AI service error. Please try again later.", "ai"));
    }
}
