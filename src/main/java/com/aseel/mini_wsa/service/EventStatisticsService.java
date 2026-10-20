package com.aseel.mini_wsa.service;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.aseel.mini_wsa.dto.AttackerStats;
import com.aseel.mini_wsa.dto.CategoryStats;
import com.aseel.mini_wsa.dto.SummaryResponse;
import com.aseel.mini_wsa.dto.TargetedPathStats;
import com.aseel.mini_wsa.dto.TimeRange;
import com.aseel.mini_wsa.exception.InvalidQueryException;
import com.aseel.mini_wsa.model.Action;
import com.aseel.mini_wsa.model.AttackCategory;
import com.aseel.mini_wsa.repository.SecurityEventRepository;

@Service
public class EventStatisticsService {

    private final SecurityEventRepository securityEventRepository;

    public EventStatisticsService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }


    public SummaryResponse summary(Integer configId, Instant from, Instant to) {

        if (from == null || to == null) {
            throw new InvalidQueryException("Both 'from' and 'to' parameters must be provided.");
        }
        if (from.isAfter(to)) {
            throw new InvalidQueryException("'from' must be before or equal to 'to'.");
        }

        long count = securityEventRepository.countForSummary(configId, from, to);

        Map<AttackCategory, CategoryStats> byCategory = new EnumMap<>(AttackCategory.class);

        for (Object[] row : securityEventRepository.summarizeCategories(configId, from, to)) {
            byCategory.put(
                    (AttackCategory) row[0],
                    new CategoryStats(
                            ((Number) row[1]).longValue(),
                            ((Number) row[2]).doubleValue()));
        }
        Map<Action, Long> byAction = new EnumMap<>(Action.class);
        for (Object[] row : securityEventRepository.summarizeActions(configId, from, to)) {
            byAction.put(
                    (Action) row[0],
                    ((Number) row[1]).longValue());
        }
        List<AttackerStats> topAttackers = securityEventRepository
                .findTopAttackers(configId, from, to, PageRequest.of(0, 10)).stream()
                .map(row -> new AttackerStats((String) row[0], ((Number) row[1]).longValue(),
                        ((Number) row[2]).doubleValue()))
                .toList();

        List<TargetedPathStats> topTargetedPaths = securityEventRepository
                .findTopTargetedPaths(configId, from, to, PageRequest.of(0, 10)).stream()
                .map(row -> new TargetedPathStats((String) row[0], ((Number) row[1]).longValue()))
                .toList();

        SummaryResponse response = new SummaryResponse(
                configId,
                new TimeRange(from, to),
                count,
                byCategory,
                byAction,
                topAttackers,
                topTargetedPaths);
        return response;

    }


}
