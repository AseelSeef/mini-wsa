package com.aseel.mini_wsa.dto;

import java.time.Instant;

import com.aseel.mini_wsa.model.Action;
import com.aseel.mini_wsa.model.GeoLocation;
import com.aseel.mini_wsa.model.SecurityEvent;
import com.aseel.mini_wsa.model.SecurityRule;

public class EnrichedSecurityEvent extends SecurityEvent {

    private String attackType;
    private int threatScore;
    private Instant receivedAt;

    public EnrichedSecurityEvent() {
        super();
    }

    public EnrichedSecurityEvent(String eventId, Instant timestamp, Integer configId, String policyId, String clientIp,
            String hostname, String path, String method, Integer statusCode, String userAgent, SecurityRule rule,
            Action action, GeoLocation geoLocation, Long requestSize, Long responseSize, String attackType,
            int threatScore, Instant receivedAt) {
        super(eventId, timestamp, configId, policyId, clientIp, hostname, path, method, statusCode, userAgent, rule,
                action, geoLocation, requestSize, responseSize);
        this.attackType = attackType;
        this.threatScore = threatScore;
        this.receivedAt = receivedAt;
    }


    public EnrichedSecurityEvent(String attackType, int threatScore, Instant receivedAt) {
        this.attackType = attackType;
        this.threatScore = threatScore;
        this.receivedAt = receivedAt;
    }


    public String getAttackType() {
        return attackType;
    }

    public void setAttackType(String attackType) {
        this.attackType = attackType;
    }

    public int getThreatScore() {
        return threatScore;
    }

    public void setThreatScore(int threatScore) {
        this.threatScore = threatScore;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }


}
