package com.aseel.mini_wsa.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.aseel.mini_wsa.dto.ValidationError;
import com.aseel.mini_wsa.model.Action;
import com.aseel.mini_wsa.model.AttackCategory;
import com.aseel.mini_wsa.model.SecurityEvent;
import com.aseel.mini_wsa.model.SecurityRule;
import com.aseel.mini_wsa.model.Severity;

import jakarta.validation.Validator;


public class EventValidationServiceTest {

    @Test
    void shouldRejectRuleWithoutSeverity() {
        // Arrange: prepare the event and validation service
        SecurityEvent event = new SecurityEvent();
        event.setRule(new SecurityRule("1", "Test Rule", "Test Message", null, AttackCategory.INJECTION));
        event.setGeoLocation(null);
        event.setEventId("event1");
        event.setTimestamp(java.time.Instant.now());
        event.setConfigId(1);
        event.setClientIp("203.0.113.42");  
        event.setPath("/test");
        event.setAction(Action.DENY);
        event.setRequestSize(100L);
        event.setResponseSize(200L);

        // Act: validate the event
        Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
        EventValidationService eventValidationService = new EventValidationService(validator);
        List<ValidationError> violations = eventValidationService.validateEvent(event);
        // Assert: check that rule.severity has an error
        assertEquals(1, violations.size());
        assertEquals("rule.severity", violations.get(0).getField());
    }

    @Test 
    void shouldAcceptValidEvent() {
        // Arrange: prepare a valid event
        SecurityEvent event = new SecurityEvent();
        event.setRule(new SecurityRule("1", "Test Rule", "Test Message", Severity.HIGH, AttackCategory.INJECTION));
        event.setGeoLocation(null);
        event.setEventId("event1");
        event.setTimestamp(java.time.Instant.now());
        event.setConfigId(1);
        event.setClientIp("203.0.113.42");  
        event.setPath("/test");
        event.setAction(Action.DENY);
        event.setRequestSize(100L);
        event.setResponseSize(200L);

        Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
        EventValidationService eventValidationService = new EventValidationService(validator);
        List<ValidationError> violations = eventValidationService.validateEvent(event);
        // Assert: check that there are no validation errors
        assertTrue(violations.isEmpty());
    }
}
