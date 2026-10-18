package com.aseel.mini_wsa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.aseel.mini_wsa.dto.ValidationError;
import com.aseel.mini_wsa.model.SecurityEvent;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class EventValidationService {

    private final Validator validator;

    public EventValidationService(Validator validator) {
        this.validator = validator;
    }

    public List<ValidationError> validateEvent(SecurityEvent event) {

        List<ValidationError> errors = new ArrayList<>();
        Set<ConstraintViolation<SecurityEvent>> violations = validator.validate(event);
        for (ConstraintViolation<SecurityEvent> violation : violations) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.add(new ValidationError(message, field));
        }
        return errors;
    }

}
