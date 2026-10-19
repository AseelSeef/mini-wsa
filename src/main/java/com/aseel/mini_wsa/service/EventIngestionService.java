package com.aseel.mini_wsa.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.aseel.mini_wsa.dto.EventIngestionResult;
import com.aseel.mini_wsa.dto.IngestionResponse;
import com.aseel.mini_wsa.dto.ValidationError;
import com.aseel.mini_wsa.entity.SecurityEventEntity;
import com.aseel.mini_wsa.exception.InvalidIngestionRequestException;
import com.aseel.mini_wsa.mapper.SecurityEventMapper;
import com.aseel.mini_wsa.model.SecurityEvent;
import com.aseel.mini_wsa.repository.SecurityEventRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service 
public class EventIngestionService {

    private final ObjectMapper objectMapper;
    private final EventValidationService eventValidationService;
    private final SecurityEventRepository securityEventRepository;
    private final EventEnrichmentService eventEnrichmentService;
    private final SecurityEventMapper securityEventMapper;

   

    public EventIngestionService(ObjectMapper objectMapper, EventValidationService eventValidationService,
            SecurityEventRepository securityEventRepository, EventEnrichmentService eventEnrichmentService,
            SecurityEventMapper securityEventMapper) {
        this.objectMapper = objectMapper;
        this.eventValidationService = eventValidationService;
        this.securityEventRepository = securityEventRepository;
        this.eventEnrichmentService = eventEnrichmentService;
        this.securityEventMapper = securityEventMapper;
    }

    public IngestionResponse ingest(JsonNode input) {
        IngestionResponse ingestionResponse = new IngestionResponse(0, 0, 0, new ArrayList<>());
        List<JsonNode> events = normalizeInput(input);
        // Process each event
        for (int index = 0; index < events.size(); index++) {
            JsonNode eventNode = events.get(index);
            EventIngestionResult result = processEvent(eventNode, index);
            if (result.getStatus() == 201) {
                    ingestionResponse.setCreated(ingestionResponse.getCreated() + 1);
                } else if(result.getStatus() == 200) {
                    ingestionResponse.setExisting(ingestionResponse.getExisting() + 1);
                } else if(result.getStatus() == 400) {
                    ingestionResponse.setRejected(ingestionResponse.getRejected() + 1);
                }
                ingestionResponse.getResults().add(result);
        }

        return ingestionResponse;
    }

    private List<JsonNode> normalizeInput(JsonNode input){
        List<JsonNode> events = new ArrayList<>();
        if (input == null || input.isNull()) {
            throw new InvalidIngestionRequestException("Input is null.");
        } else if (input.isArray() && input.size() > 0) {
            for (JsonNode eventNode : input) {
                events.add(eventNode);
            }
        } else if (input.isObject()) {
            events.add(input);
        } else {
            throw new InvalidIngestionRequestException("Expected a JSON object or a non-empty array.");
        }
        return events;
    }

    private EventIngestionResult processEvent(JsonNode eventNode, int index){
        if (eventNode == null || !eventNode.isObject()) {
            return new EventIngestionResult(index, null, 400,
                List.of(new ValidationError("Expected an event object", "event")));
        }
        String eventId = eventNode.has("eventId") && !eventNode.get("eventId").isNull() && eventNode.get("eventId").isString() ? eventNode.get("eventId").asString() : null;
        try {
            SecurityEvent event = objectMapper.treeToValue(eventNode, SecurityEvent.class);
            List<ValidationError> errors = eventValidationService.validateEvent(event);
            if (errors.isEmpty()) {
                // Event is valid, process it (e.g., save to database)
                SecurityEventEntity existingEvent = securityEventRepository.findById(event.getEventId()).orElse(null);
                if (existingEvent != null) {
                    EventIngestionResult result = new EventIngestionResult(index, existingEvent.getEventId(), 200, List.of());
                    result.setEvent(securityEventMapper.toResponse(existingEvent));
                    return result;
                }
                long previousCount = securityEventRepository.countByClientIpAndTimestampBetween(event.getClientIp(), event.getTimestamp().minusSeconds(600), event.getTimestamp());
                SecurityEventEntity entity = securityEventMapper.toEntity(event);
                entity.setReceivedAt(Instant.now().truncatedTo(ChronoUnit.MICROS));
                entity.setAttackType(eventEnrichmentService.classify(event.getRule().getCategory()));
                entity.setThreatScore(eventEnrichmentService.calculateThreatScore(event, previousCount));
                SecurityEventEntity savedEntity = securityEventRepository.saveAndFlush(entity);
                EventIngestionResult result = new EventIngestionResult(index, savedEntity.getEventId(), 201, List.of());
                result.setEvent(securityEventMapper.toResponse(savedEntity));
                return result;
            } else {
                // Event is invalid, add to rejected count and results
                return new EventIngestionResult(index,event.getEventId(), 400, errors);
            }
        } catch (tools.jackson.databind.exc.InvalidFormatException e) {
            // Handle enum or other format exceptions
            String fieldName = e.getPath().stream()
                .map(path -> path.getPropertyName())
                .filter(name -> name != null)
                .collect(Collectors.joining("."));

            if (fieldName.isEmpty()) {
                fieldName = "event";
            }
            String message = "Invalid event field type, enum value, or timestamp.";
            if(e.getTargetType() != null && e.getTargetType().isEnum()){
                String validValues = Arrays.stream(e.getTargetType().getEnumConstants())
                    .map(value -> ((Enum<?>) value).name())
                    .collect(Collectors.joining(", "));  
                message = "Invalid enum value. Valid values are: " + validValues;
            }
            return new EventIngestionResult(index,eventId ,400,List.of(new ValidationError(message, fieldName)));
        }
        catch (tools.jackson.databind.exc.MismatchedInputException e) {
            // Handle deserialization or other exceptions
            return new EventIngestionResult(index,eventId ,400,List.of(new ValidationError("Invalid event field type, enum value, or timestamp.", "event")));
        }

    }
}
