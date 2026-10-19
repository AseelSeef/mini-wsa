package com.aseel.mini_wsa.service;

import org.junit.jupiter.api.Test;

import com.aseel.mini_wsa.model.Action;
import com.aseel.mini_wsa.model.AttackCategory;
import com.aseel.mini_wsa.model.SecurityEvent;
import com.aseel.mini_wsa.model.SecurityRule;
import com.aseel.mini_wsa.model.Severity;

public class EventEnrichmentServiceTest {

    @Test
    public void testClassify() {
        // Test cases for the classify method
         SecurityEvent event = new SecurityEvent();
        event.setRule(new SecurityRule("1", "Test Rule", "Test Message", Severity.HIGH, AttackCategory.INJECTION));
        event.setGeoLocation(null);
        event.setEventId("event1");
        event.setTimestamp(java.time.Instant.now());
        event.setConfigId(1);
        event.setClientIp("203.0.113.42");  
        event.setPath("/login");
        event.setAction(Action.DENY);
        event.setRequestSize(100L);
        event.setResponseSize(200L);

        EventEnrichmentService enrichmentService = new EventEnrichmentService();
        assert enrichmentService.classify(event.getRule().getCategory()).equals("SQL/Command Injection");
        // assert enrichmentService.classify(AttackCategory.XSS).equals("Cross-Site Scripting");
        // assert enrichmentService.classify(AttackCategory.PROTOCOL_VIOLATION).equals("Protocol Anomaly");
        // assert enrichmentService.classify(AttackCategory.DATA_LEAKAGE).equals("Data Exfiltration");
        // assert enrichmentService.classify(AttackCategory.BOT).equals("Bot Activity");
        // assert enrichmentService.classify(AttackCategory.DOS).equals("Denial of Service");
        // assert enrichmentService.classify(AttackCategory.RATE_LIMIT).equals("Rate Limiting");
        // assert enrichmentService.classify(null).equals("Unknown Attack Category");  

        assert enrichmentService.calculateThreatScore(event, 0) == 65; // 30 (HIGH) + 15 (DENY)
        assert enrichmentService.calculateThreatScore(event, 5) == 80; // 30
        assert enrichmentService.calculateThreatScore(event, 4) == 65; // 30
        
    }

}
