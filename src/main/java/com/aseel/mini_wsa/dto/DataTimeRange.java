package com.aseel.mini_wsa.dto;

import java.time.Instant;

/**
 * Timestamps of the earliest and latest stored events, both inclusive.
 * Both are null when no events match.
 */
public record DataTimeRange(Instant earliest, Instant latest, long totalEvents) {
}
