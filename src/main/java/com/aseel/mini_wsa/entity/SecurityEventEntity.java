package com.aseel.mini_wsa.entity;

import java.time.Instant;

import com.aseel.mini_wsa.model.AttackCategory;
import com.aseel.mini_wsa.model.Severity;
import com.aseel.mini_wsa.model.Action;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "security_events", indexes = {
        @Index(name = "idx_events_ip_time", columnList = "client_ip,event_timestamp"),
        @Index(name = "idx_events_config_time", columnList = "config_id,event_timestamp"),
        @Index(name = "idx_events_time", columnList = "event_timestamp")
})
public class SecurityEventEntity {

    @Id
    @Column(name = "event_id", columnDefinition = "text")   
    private String eventId;
    @Column(name = "event_timestamp", nullable = false)
    private Instant timestamp;
    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;
    @Column(name = "config_id", nullable = false)
    private Integer configId;
    @Column(name = "status_code")
    private Integer statusCode;
    @Column(name = "threat_score", nullable = false)
    private Integer threatScore;
    @Column(name = "request_size")
    private Long requestSize;
    @Column(name = "response_size")
    private Long responseSize;
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private Action action;
    @Enumerated(EnumType.STRING)
    @Column(name = "rule_severity", nullable = false)
    private Severity ruleSeverity;
    @Enumerated(EnumType.STRING)
    @Column(name = "rule_category", nullable = false)
    private AttackCategory ruleCategory;
    @Column(name = "policy_id", columnDefinition = "text")
    private String policyId;
    @Column(name = "client_ip", nullable = false, columnDefinition = "text")
    private String clientIp;
    @Column(name = "hostname", columnDefinition = "text")
    private String hostname;
    @Column(name = "path", nullable = false, columnDefinition = "text")
    private String path;
    @Column(name = "method", columnDefinition = "text")
    private String method;
    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;
    @Column(name = "rule_id", columnDefinition = "text")
    private String ruleId;
    @Column(name = "rule_name", columnDefinition = "text")
    private String ruleName;
    @Column(name = "rule_message", columnDefinition = "text")
    private String ruleMessage;
    @Column(name = "geo_country", columnDefinition = "text")
    private String geoCountry;
    @Column(name = "geo_city", columnDefinition = "text")
    private String geoCity;
    @Column(name = "attack_type", nullable = false, columnDefinition = "text")
    private String attackType;


    public SecurityEventEntity() {
    }

    public String getEventId() {
        return eventId;
    }


    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public Instant getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }


    public Instant getReceivedAt() {
        return receivedAt;
    }


    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }


    public Integer getConfigId() {
        return configId;
    }


    public void setConfigId(Integer configId) {
        this.configId = configId;
    }


    public Integer getStatusCode() {
        return statusCode;
    }


    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }


    public Integer getThreatScore() {
        return threatScore;
    }


    public void setThreatScore(Integer threatScore) {
        this.threatScore = threatScore;
    }


    public Long getRequestSize() {
        return requestSize;
    }


    public void setRequestSize(Long requestSize) {
        this.requestSize = requestSize;
    }


    public Long getResponseSize() {
        return responseSize;
    }


    public void setResponseSize(Long responseSize) {
        this.responseSize = responseSize;
    }


    public Action getAction() {
        return action;
    }


    public void setAction(Action action) {
        this.action = action;
    }


    public Severity getRuleSeverity() {
        return ruleSeverity;
    }


    public void setRuleSeverity(Severity ruleSeverity) {
        this.ruleSeverity = ruleSeverity;
    }


    public AttackCategory getRuleCategory() {
        return ruleCategory;
    }


    public void setRuleCategory(AttackCategory ruleCategory) {
        this.ruleCategory = ruleCategory;
    }


    public String getPolicyId() {
        return policyId;
    }


    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }


    public String getClientIp() {
        return clientIp;
    }


    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }


    public String getHostname() {
        return hostname;
    }


    public void setHostname(String hostname) {
        this.hostname = hostname;
    }


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getMethod() {
        return method;
    }


    public void setMethod(String method) {
        this.method = method;
    }


    public String getUserAgent() {
        return userAgent;
    }


    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }


    public String getRuleId() {
        return ruleId;
    }


    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }


    public String getRuleName() {
        return ruleName;
    }


    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }


    public String getRuleMessage() {
        return ruleMessage;
    }


    public void setRuleMessage(String ruleMessage) {
        this.ruleMessage = ruleMessage;
    }


    public String getGeoCountry() {
        return geoCountry;
    }


    public void setGeoCountry(String geoCountry) {
        this.geoCountry = geoCountry;
    }


    public String getGeoCity() {
        return geoCity;
    }


    public void setGeoCity(String geoCity) {
        this.geoCity = geoCity;
    }


    public String getAttackType() {
        return attackType;
    }


    public void setAttackType(String attackType) {
        this.attackType = attackType;
    }

    

    

}
