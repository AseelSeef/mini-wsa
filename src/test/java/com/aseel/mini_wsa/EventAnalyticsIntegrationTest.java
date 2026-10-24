package com.aseel.mini_wsa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.aseel.mini_wsa.dto.AttackerStats;
import com.aseel.mini_wsa.dto.EnrichedSecurityEvent;
import com.aseel.mini_wsa.dto.SamplesResponse;
import com.aseel.mini_wsa.dto.SummaryResponse;
import com.aseel.mini_wsa.dto.TargetedPathStats;
import com.aseel.mini_wsa.entity.SecurityEventEntity;
import com.aseel.mini_wsa.exception.InvalidQueryException;
import com.aseel.mini_wsa.model.Action;
import com.aseel.mini_wsa.model.AttackCategory;
import com.aseel.mini_wsa.model.Severity;
import com.aseel.mini_wsa.repository.SecurityEventRepository;
import com.aseel.mini_wsa.service.EventSamplesService;
import com.aseel.mini_wsa.service.EventStatisticsService;

/**
 * Query integration tests: real services and PostgreSQL, without repository mocks.
 * Fixtures have explicit scores so query correctness is independent of enrichment.
 * Service calls run on the test thread: @Transactional rolls back fixture writes.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EventAnalyticsIntegrationTest {

    private static final Instant FROM = Instant.parse("2045-01-01T10:00:00Z");
    private static final Instant TO = FROM.plusSeconds(60);

    @Autowired
    private SecurityEventRepository repository;

    @Autowired
    private EventStatisticsService statistics;

    @Autowired
    private EventSamplesService samples;

    private int configId;
    private int otherConfigId;
    private String prefix;

    @BeforeEach
    void isolateFixtures() {
        configId = -ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE - 1);
        otherConfigId = configId - 1;
        prefix = "analytics-" + UUID.randomUUID() + "-";
    }

    @Test
    void shouldAggregateKnownDataAndApplyConfigurationAndTimeFilters() {
        save("a", configId, FROM, AttackCategory.INJECTION, Action.DENY,
            "192.0.2.10", "/login", 20);
        save("b", configId, FROM.plusSeconds(10), AttackCategory.INJECTION, Action.DENY,
            "192.0.2.10", "/login", 40);
        save("c", configId, FROM.plusSeconds(20), AttackCategory.BOT, Action.ALERT,
            "192.0.2.11", "/search", 60);
        save("d", configId, FROM.plusSeconds(30), AttackCategory.BOT, Action.MONITOR,
            "192.0.2.12", "/products", 80);

        // Decoys must affect none of the returned aggregates.
        save("other-config", otherConfigId, FROM, AttackCategory.XSS, Action.DENY,
            "192.0.2.99", "/excluded", 100);
        save("before-from", configId, FROM.minusSeconds(1), AttackCategory.XSS, Action.DENY,
            "192.0.2.99", "/excluded", 100);
        save("at-to", configId, TO, AttackCategory.XSS, Action.DENY,
            "192.0.2.99", "/excluded", 100);
        repository.flush();

        SummaryResponse response = statistics.summary(configId, FROM, TO);

        assertEquals(4L, response.getTotalEvents());
        assertEquals(2, response.getByCategory().size());
        assertEquals(2L, response.getByCategory().get(AttackCategory.INJECTION).getCount());
        assertEquals(30.0, response.getByCategory().get(AttackCategory.INJECTION)
            .getAvgThreatScore(), 0.000001);
        assertEquals(2L, response.getByCategory().get(AttackCategory.BOT).getCount());
        assertEquals(70.0, response.getByCategory().get(AttackCategory.BOT)
            .getAvgThreatScore(), 0.000001);

        assertEquals(3, response.getByAction().size());
        assertEquals(Long.valueOf(2), response.getByAction().get(Action.DENY));
        assertEquals(Long.valueOf(1), response.getByAction().get(Action.ALERT));
        assertEquals(Long.valueOf(1), response.getByAction().get(Action.MONITOR));

        assertEquals(List.of("192.0.2.10", "192.0.2.11", "192.0.2.12"),
            response.getTopAttackers().stream().map(AttackerStats::getClientIp).toList());
        assertEquals(List.of(2L, 1L, 1L),
            response.getTopAttackers().stream().map(AttackerStats::getCount).toList());
        assertEquals(30.0, response.getTopAttackers().get(0).getAvgThreatScore(), 0.000001);

        assertEquals(List.of("/login", "/products", "/search"),
            response.getTopTargetedPaths().stream().map(TargetedPathStats::getPath).toList());
        assertEquals(List.of(2L, 1L, 1L),
            response.getTopTargetedPaths().stream().map(TargetedPathStats::getCount).toList());
    }

    @Test
    void shouldLimitBothTopListsToTenAndOrderCountsAndTies() {
        // Insert in reverse key order, so insertion order cannot satisfy the test.
        for (int group = 11; group >= 0; group--) {
            int count = group < 2 ? 3 : 2;
            for (int item = 0; item < count; item++) {
                save("top-" + group + "-" + item, configId, FROM.plusSeconds(item),
                    AttackCategory.BOT, Action.DENY, "192.0.2." + (10 + group),
                    "/target-%02d".formatted(group), 20 + group);
            }
        }
        repository.flush();

        SummaryResponse response = statistics.summary(configId, FROM, TO);

        assertEquals(26L, response.getTotalEvents());
        assertEquals(10, response.getTopAttackers().size());
        assertEquals(10, response.getTopTargetedPaths().size());
        assertEquals(IntStream.range(0, 10).mapToObj(i -> "192.0.2." + (10 + i)).toList(),
            response.getTopAttackers().stream().map(AttackerStats::getClientIp).toList());
        assertEquals(IntStream.range(0, 10).mapToObj(i -> "/target-%02d".formatted(i)).toList(),
            response.getTopTargetedPaths().stream().map(TargetedPathStats::getPath).toList());

        for (int i = 0; i < 10; i++) {
            long expectedCount = i < 2 ? 3L : 2L;
            assertEquals(expectedCount, response.getTopAttackers().get(i).getCount());
            assertEquals(expectedCount, response.getTopTargetedPaths().get(i).getCount());
            assertEquals(20.0 + i, response.getTopAttackers().get(i).getAvgThreatScore(),
                0.000001);
        }
    }

    @Test
    void shouldFilterSamplesAndPreserveTotalAcrossArbitraryOffsets() {
        seedSamples();

        SamplesResponse first = samples.samples(configId, FROM, TO,
            AttackCategory.BOT, Action.DENY, 2, 0);
        assertEquals(5L, first.getTotal());
        assertEquals(2, first.getLimit());
        assertEquals(0, first.getOffset());
        assertEquals(List.of(prefix + "e", prefix + "c"), ids(first));

        // Offset 1 is not a multiple of the page size.
        SamplesResponse middle = samples.samples(configId, FROM, TO,
            AttackCategory.BOT, Action.DENY, 2, 1);
        assertEquals(5L, middle.getTotal());
        assertEquals(List.of(prefix + "c", prefix + "d"), ids(middle));
        assertEquals(1, middle.getOffset());

        SamplesResponse last = samples.samples(configId, FROM, TO,
            AttackCategory.BOT, Action.DENY, 2, 4);
        assertEquals(5L, last.getTotal());
        assertEquals(List.of(prefix + "a"), ids(last));

        SamplesResponse beyond = samples.samples(configId, FROM, TO,
            AttackCategory.BOT, Action.DENY, 2, 20);
        assertEquals(5L, beyond.getTotal());
        assertTrue(beyond.getEvents().isEmpty());
    }

    @Test
    void shouldApplyOptionalSampleFiltersIndependently() {
        seedSamples();

        // Within the time range: five BOT/DENY, one XSS/DENY, one BOT/ALERT.
        assertEquals(7L, samples.samples(configId, FROM, TO, null, null, 100, 0).getTotal());
        assertEquals(6L, samples.samples(configId, FROM, TO,
            AttackCategory.BOT, null, 100, 0).getTotal());
        assertEquals(6L, samples.samples(configId, FROM, TO,
            null, Action.DENY, 100, 0).getTotal());

        // No time bounds: also include the event before FROM and the one at TO.
        assertEquals(9L, samples.samples(configId, null, null, null, null, 100, 0).getTotal());
        assertEquals(8L, samples.samples(configId, FROM, null, null, null, 100, 0).getTotal());
        assertEquals(8L, samples.samples(configId, null, TO, null, null, 100, 0).getTotal());

        SamplesResponse none = samples.samples(configId, FROM, TO,
            AttackCategory.DOS, null, 100, 0);
        assertEquals(0L, none.getTotal());
        assertTrue(none.getEvents().isEmpty());
    }

    @Test
    void shouldReturnEmptySummaryForAnEmptyFilteredRange() {
        save("outside", configId, TO, AttackCategory.BOT, Action.DENY,
            "192.0.2.10", "/login", 20);
        repository.flush();

        SummaryResponse response = statistics.summary(configId, FROM, TO);
        assertEquals(0L, response.getTotalEvents());
        assertTrue(response.getByCategory().isEmpty());
        assertTrue(response.getByAction().isEmpty());
        assertTrue(response.getTopAttackers().isEmpty());
        assertTrue(response.getTopTargetedPaths().isEmpty());
    }

    @Test
    void shouldRejectInvalidQueryRangesAndPagination() {
        assertThrows(InvalidQueryException.class,
            () -> statistics.summary(configId, TO, FROM));
        assertThrows(InvalidQueryException.class,
            () -> samples.samples(configId, FROM, TO, null, null, 0, 0));
        assertThrows(InvalidQueryException.class,
            () -> samples.samples(configId, FROM, TO, null, null, 101, 0));
        assertThrows(InvalidQueryException.class,
            () -> samples.samples(configId, FROM, TO, null, null, 20, -1));
        assertThrows(InvalidQueryException.class,
            () -> samples.samples(configId, TO, FROM, null, null, 20, 0));
    }

    private void seedSamples() {
        save("e", configId, FROM.plusSeconds(30), AttackCategory.BOT, Action.DENY,
            "192.0.2.10", "/login", 20);
        save("d", configId, FROM.plusSeconds(20), AttackCategory.BOT, Action.DENY,
            "192.0.2.10", "/login", 20);
        save("c", configId, FROM.plusSeconds(20), AttackCategory.BOT, Action.DENY,
            "192.0.2.10", "/login", 20);
        save("b", configId, FROM.plusSeconds(10), AttackCategory.BOT, Action.DENY,
            "192.0.2.10", "/login", 20);
        save("a", configId, FROM, AttackCategory.BOT, Action.DENY,
            "192.0.2.10", "/login", 20);
        save("wrong-category", configId, FROM, AttackCategory.XSS, Action.DENY,
            "192.0.2.10", "/login", 20);
        save("wrong-action", configId, FROM, AttackCategory.BOT, Action.ALERT,
            "192.0.2.10", "/login", 20);
        save("wrong-config", otherConfigId, FROM, AttackCategory.BOT, Action.DENY,
            "192.0.2.10", "/login", 20);
        save("before-from", configId, FROM.minusSeconds(1), AttackCategory.BOT, Action.DENY,
            "192.0.2.10", "/login", 20);
        save("at-to", configId, TO, AttackCategory.BOT, Action.DENY,
            "192.0.2.10", "/login", 20);
        repository.flush();
    }

    private void save(String suffix, int config, Instant timestamp, AttackCategory category,
            Action action, String ip, String path, int score) {
        SecurityEventEntity entity = new SecurityEventEntity();
        entity.setEventId(prefix + suffix);
        entity.setConfigId(config);
        entity.setTimestamp(timestamp);
        entity.setReceivedAt(timestamp);
        entity.setRuleCategory(category);
        entity.setRuleSeverity(Severity.HIGH);
        entity.setAction(action);
        entity.setClientIp(ip);
        entity.setPath(path);
        entity.setAttackType("Query fixture");
        entity.setThreatScore(score);
        repository.save(entity);
    }

    private List<String> ids(SamplesResponse response) {
        return response.getEvents().stream().map(EnrichedSecurityEvent::getEventId).toList();
    }
}
