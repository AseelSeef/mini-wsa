package com.aseel.mini_wsa.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aseel.mini_wsa.dto.EnrichedSecurityEvent;
import com.aseel.mini_wsa.dto.SamplesResponse;
import com.aseel.mini_wsa.exception.InvalidQueryException;
import com.aseel.mini_wsa.mapper.SecurityEventMapper;
import com.aseel.mini_wsa.model.Action;
import com.aseel.mini_wsa.model.AttackCategory;
import com.aseel.mini_wsa.repository.EventSamplesRepository;

@Service 
public class EventSamplesService {

    private final EventSamplesRepository eventSamplesRepository;
    private final SecurityEventMapper securityEventMapper;

    public EventSamplesService(EventSamplesRepository eventSamplesRepository, SecurityEventMapper securityEventMapper) {
        this.eventSamplesRepository = eventSamplesRepository;
        this.securityEventMapper = securityEventMapper;
    }

    public SamplesResponse samples(Integer configId,Instant from,Instant to,AttackCategory category,Action action,int limit,int offset) {
        if (limit < 1 || limit > 100) {
            throw new InvalidQueryException("'limit' must be between 1 and 100.");
        }
        if (offset < 0) {
            throw new InvalidQueryException("'offset' must be zero or greater.");
        }
        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidQueryException("'from' must be before or equal to 'to'.");
        }

        long total = eventSamplesRepository.count(configId, from, to, category, action);

        List<EnrichedSecurityEvent> events = eventSamplesRepository.findSamples(
                configId, from, to, category, action, limit, offset)
            .stream()
            .map(securityEventMapper::toResponse)
            .toList();

        return new SamplesResponse(events, total, limit, offset);

    }

}
