package com.aseel.mini_wsa.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aseel.mini_wsa.dto.DataTimeRange;
import com.aseel.mini_wsa.dto.SummaryResponse;
import com.aseel.mini_wsa.service.EventStatisticsService;

class SecurityAnalyticsToolsTest {

    private EventStatisticsService statistics;
    private SecurityAnalyticsTools tools;

    @BeforeEach
    void setUp() {
        statistics = mock(EventStatisticsService.class);
        tools = new SecurityAnalyticsTools(statistics);
    }

    @Test
    void getSummaryParsesIsoTimestampsAndDelegates() {
        SummaryResponse expected = new SummaryResponse();
        Instant from = Instant.parse("2026-11-07T00:00:00Z");
        Instant to = Instant.parse("2026-11-07T12:00:00Z");
        when(statistics.summary(42, from, to)).thenReturn(expected);

        SummaryResponse actual = tools.getSummary(42, "2026-11-07T00:00:00Z", "2026-11-07T12:00:00Z");

        assertSame(expected, actual);
    }

    @Test
    void getSummaryPassesNullConfigIdForAllConfigurations() {
        tools.getSummary(null, "2026-11-07T00:00:00Z", "2026-11-07T12:00:00Z");

        verify(statistics).summary(null,
                Instant.parse("2026-11-07T00:00:00Z"), Instant.parse("2026-11-07T12:00:00Z"));
    }

    @Test
    void getSummaryRejectsNonIsoTimestampWithMessageForTheModel() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> tools.getSummary(null, "yesterday", "2026-11-07T12:00:00Z"));

        assertTrue(error.getMessage().contains("Invalid from value 'yesterday'"), error.getMessage());
        assertTrue(error.getMessage().contains("ISO-8601"), error.getMessage());
        verify(statistics, never()).summary(any(), any(), any());
    }

    @Test
    void getSummaryRejectsMissingTimestamp() {
        IllegalArgumentException nullTo = assertThrows(IllegalArgumentException.class,
                () -> tools.getSummary(null, "2026-11-07T00:00:00Z", null));
        IllegalArgumentException blankFrom = assertThrows(IllegalArgumentException.class,
                () -> tools.getSummary(null, " ", "2026-11-07T12:00:00Z"));

        assertTrue(nullTo.getMessage().startsWith("Missing to"), nullTo.getMessage());
        assertTrue(blankFrom.getMessage().startsWith("Missing from"), blankFrom.getMessage());
        verify(statistics, never()).summary(any(), any(), any());
    }

    @Test
    void getDataTimeRangeDelegates() {
        DataTimeRange expected = new DataTimeRange(
                Instant.parse("2026-11-01T00:00:00Z"), Instant.parse("2026-11-02T00:00:00Z"), 7);
        when(statistics.dataTimeRange(42)).thenReturn(expected);

        assertEquals(expected, tools.getDataTimeRange(42));
    }
}
