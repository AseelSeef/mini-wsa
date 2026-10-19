package com.aseel.mini_wsa.service;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.aseel.mini_wsa.dto.IngestionResponse;

import jakarta.validation.Validator;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;


public class EventIngestionServiceTest {

    @Test
    void shouldContinueBatchAfterInvalidAction() {
        ObjectMapper objectMapper = JsonMapper.builder().build();
        JsonNode event1 = objectMapper.readTree("""
            {
            "eventId": "event1",
            "timestamp": "2023-08-01T12:00:00Z",
            "configId": 1,
            "clientIp": "203.0.113.42",
            "path": "test",
            "action": "BLOCK",
            "rule": {
                "severity": "HIGH",
                "category": "INJECTION"
            },
            "requestSize": 100,
            "responseSize": 200
            }
            """);

        JsonNode event2 = objectMapper.readTree("""
            {
            "eventId": "event2",
            "timestamp": "2023-08-01T12:00:00Z",
            "configId": 1,
            "clientIp": "203.0.113.42",
            "path": "test",
            "action": "DENY",
            "rule": {
                "severity": "HIGH",
                "category": "INJECTION"
            },
            "requestSize": 100,
            "responseSize": 200
            }
            """);        
        JsonNode inputs = objectMapper.createArrayNode().add(event1).add(event2);

        Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
      
        EventIngestionService eventIngestionService = new EventIngestionService(objectMapper, new EventValidationService(validator));
        IngestionResponse response = eventIngestionService.ingest(inputs);
        assertEquals(1, response.getRejected());
        assertEquals(1, response.getCreated());
        assertEquals(0, response.getExisting());
        assertEquals(2, response.getResults().size());
        assertEquals(400, response.getResults().get(0).getStatus());
        assertEquals(201, response.getResults().get(1).getStatus());
        assertEquals("action",response.getResults().get(0).getValidationErrors().get(0).getField());    
    }
}
