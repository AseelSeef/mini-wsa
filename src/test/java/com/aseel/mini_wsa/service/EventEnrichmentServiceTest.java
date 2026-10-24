package com.aseel.mini_wsa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.aseel.mini_wsa.model.Action;
import com.aseel.mini_wsa.model.AttackCategory;
import com.aseel.mini_wsa.model.SecurityEvent;
import com.aseel.mini_wsa.model.SecurityRule;
import com.aseel.mini_wsa.model.Severity;


public class EventEnrichmentServiceTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
        "INJECTION|SQL/Command Injection",
        "XSS|Cross-Site Scripting",
        "PROTOCOL_VIOLATION|Protocol Anomaly",
        "DATA_LEAKAGE|Data Exfiltration",
        "BOT|Bot Activity",
        "DOS|Denial of Service",
        "RATE_LIMIT|Rate Limiting"
    })
    void shouldClassifyEveryCategory(AttackCategory category, String expectedAttackType) {

        EventEnrichmentService service = new EventEnrichmentService();

        assertEquals(expectedAttackType, service.classify(category));
    }

    @ParameterizedTest
    @CsvSource({
            "CRITICAL, MONITOR, /public,      0, 40",
            "HIGH,     MONITOR, /public,      0, 30",
            "MEDIUM,   MONITOR, /public,      0, 20",
            "LOW,      MONITOR, /public,      0, 10",
            "LOW,      DENY,    /public,      0, 30",
            "LOW,      ALERT,   /public,      0, 20",
            "HIGH,     DENY,    /api/login,   0, 65",
            "HIGH,     DENY,    /admin/users, 0, 65",
            "HIGH,     DENY,    /admin/login, 0, 65",
            "HIGH,     DENY,    /api/login,   4, 65",
            "HIGH,     DENY,    /api/login,   5, 80",
            "HIGH,     DENY,    /api/login,   6, 80",
            "HIGH,     DENY,    /public,      5, 65",
            "CRITICAL, DENY,    /admin,       5, 90"
    })
    void shouldCalculateThreatScore(Severity severity,Action action,String path,long previousCount,int expectedScore) {

        SecurityEvent event = new SecurityEvent();
        event.setRule(new SecurityRule("rule-1", "Test Rule", "Test Message",severity, AttackCategory.INJECTION));
        event.setAction(action);
        event.setPath(path);

        EventEnrichmentService service = new EventEnrichmentService();

        assertEquals(expectedScore,service.calculateThreatScore(event, previousCount));
    }
}
