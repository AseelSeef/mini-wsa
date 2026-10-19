package com.aseel.mini_wsa.service;

import org.springframework.stereotype.Service;

import com.aseel.mini_wsa.model.AttackCategory;
import com.aseel.mini_wsa.model.SecurityEvent;

@Service 
public class EventEnrichmentService{

    public String classify(AttackCategory category){
        switch(category){
            case INJECTION:
                return "SQL/Command Injection";
            case XSS:
                return "Cross-Site Scripting";
            case PROTOCOL_VIOLATION:
                return "Protocol Anomaly";
            case DATA_LEAKAGE:
                return "Data Exfiltration";
            case BOT:
                return "Bot Activity";
            case DOS:
                return "Denial of Service";
            case RATE_LIMIT:
                return "Rate Limiting";
            default:
                return "Unknown Attack Category";
        }
    }

    public int calculateThreatScore(SecurityEvent event, long previousCount){
        int baseScore = 0;
        baseScore += calculateSeverityScore(event);
        baseScore += calculateActionScore(event);
        
        if (event.getPath().contains("/admin") || event.getPath().contains("/login")) {
            baseScore += 15;
        }

        if(previousCount >= 5){
            baseScore += 15; 
        }

        return Math.min(baseScore, 100);
    }

    private int calculateSeverityScore(SecurityEvent event){
        switch(event.getRule().getSeverity()){
            case CRITICAL:
                return 40;
            case HIGH:
                return 30;
            case MEDIUM:
                return 20;
            case LOW:
                return 10;
            default:
                return 0;
        }
    }

    private int calculateActionScore(SecurityEvent event){
        switch(event.getAction()){
            case DENY:
                return 20;
            case ALERT:
                return 10;
            case MONITOR:
                return 0;
            default:
                return 0;
        }
    }

}
