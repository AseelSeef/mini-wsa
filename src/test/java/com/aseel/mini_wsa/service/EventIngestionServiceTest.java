package com.aseel.mini_wsa.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import com.aseel.mini_wsa.dto.EventIngestionResult;
import com.aseel.mini_wsa.dto.IngestionResponse;
import com.aseel.mini_wsa.entity.SecurityEventEntity;
import com.aseel.mini_wsa.mapper.SecurityEventMapper;
import com.aseel.mini_wsa.repository.EventInsertRepository;
import com.aseel.mini_wsa.repository.SecurityEventRepository;

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
        
        SecurityEventRepository repository =Mockito.mock(SecurityEventRepository.class);
        EventInsertRepository eventInsertRepository = Mockito.mock(EventInsertRepository.class);

        EventIngestionService eventIngestionService = new EventIngestionService(objectMapper, new EventValidationService(validator),repository,new EventEnrichmentService(),new SecurityEventMapper(), eventInsertRepository);
        IngestionResponse response = eventIngestionService.ingest(inputs);
        assertEquals(1, response.getRejected());
        assertEquals(1, response.getCreated());
        assertEquals(0, response.getExisting());
        assertEquals(2, response.getResults().size());
        assertEquals(400, response.getResults().get(0).getStatus());
        assertEquals(201, response.getResults().get(1).getStatus());
        assertEquals("action",response.getResults().get(0).getValidationErrors().get(0).getField());    
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnStoredEventWhenConcurrentInsertLoses() {
        ObjectMapper objectMapper = JsonMapper.builder().build();
        JsonNode event = objectMapper.readTree("""
                {
                "eventId": "event",
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

        Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();

        SecurityEventRepository repository = Mockito.mock(SecurityEventRepository.class);
        // dummy entity to mimic the stored event in the database
        String eventId = event.get("eventId").asString();
        SecurityEventEntity storedEntity = new SecurityEventEntity();
        storedEntity.setEventId(eventId);
        storedEntity.setThreatScore(80);
        storedEntity.setReceivedAt(Instant.parse("2023-08-01T12:01:00Z"));

        // First lookup: the event does not exist yet.
        // Second lookup: another request has inserted it in the meantime.
        when(repository.findById(eventId)).thenReturn(Optional.empty(), Optional.of(storedEntity));

        EventInsertRepository eventInsertRepository = Mockito.mock(EventInsertRepository.class);

        // Simulate losing the insert race: the same ID was inserted by another request.
        doThrow(new DataIntegrityViolationException("Duplicate event ID")).when(eventInsertRepository)
                .insert(any(SecurityEventEntity.class));

        EventIngestionService eventIngestionService = new EventIngestionService(objectMapper,
                new EventValidationService(validator), repository, new EventEnrichmentService(),
                new SecurityEventMapper(), eventInsertRepository);
        IngestionResponse response = eventIngestionService.ingest(event);

        assertEquals(0, response.getCreated());
        assertEquals(1, response.getExisting());
        assertEquals(0, response.getRejected());

        EventIngestionResult result = response.getResults().get(0);
        assertEquals(200, result.getStatus());
        assertEquals(eventId, result.getEvent().getEventId());
        assertEquals(Integer.valueOf(80), result.getEvent().getThreatScore());
        assertEquals(storedEntity.getReceivedAt(), result.getEvent().getReceivedAt());

    }
}
