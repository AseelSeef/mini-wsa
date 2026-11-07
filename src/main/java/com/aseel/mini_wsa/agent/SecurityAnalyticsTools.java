package com.aseel.mini_wsa.agent;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.aseel.mini_wsa.dto.DataTimeRange;
import com.aseel.mini_wsa.dto.SummaryResponse;
import com.aseel.mini_wsa.service.EventStatisticsService;

@Component
public class SecurityAnalyticsTools {
    private final EventStatisticsService eventStatisticsService;


    public SecurityAnalyticsTools(EventStatisticsService eventStatisticsService) {
        this.eventStatisticsService = eventStatisticsService;
    }

    @Tool(description = """
            Get aggregated security statistics for a time window: total events,
            counts and average threat score per attack category, counts per action,
            top 10 attacker IPs and top 10 targeted paths.
            The window is [from, to): from is inclusive, to is exclusive.""")
    public SummaryResponse getSummary(
            @ToolParam(description = "Configuration ID to filter by. Omit for all configurations.", required = false) Integer configId,
            @ToolParam(description = "Start of window, ISO-8601 UTC, e.g. 2026-11-07T00:00:00Z") String from,
            @ToolParam(description = "End of window, ISO-8601 UTC, e.g. 2026-11-07T12:00:00Z") String to) {

        Instant fromInstant = parseInstant("from",from);
        Instant toInstant = parseInstant("to",to);
        return eventStatisticsService.summary(configId, fromInstant, toInstant);
    }

    private Instant parseInstant(String name, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing " + name + ". Use ISO-8601 UTC format, e.g. 2026-11-07T00:00:00Z.");
        }
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid " + name + " value '" + value + "'. Use ISO-8601 UTC format, e.g. 2026-11-07T00:00:00Z.",
                    e);
        }
    }

    @Tool(description = """
            Get the timestamps of the earliest and latest stored events and the total event count.
            Call this first when the question does not give an exact time window, or when another
            tool returns no data, to find out which period actually contains events.
            earliest and latest are null when there are no events.
            latest is inclusive: when using it as the 'to' of another tool, add one second.""")
    public DataTimeRange getDataTimeRange(
            @ToolParam(description = "Configuration ID to filter by. Omit for all configurations.", required = false) Integer configId) {
        return eventStatisticsService.dataTimeRange(configId);
    }

}
