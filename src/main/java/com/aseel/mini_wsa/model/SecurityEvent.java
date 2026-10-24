package com.aseel.mini_wsa.model;

import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class SecurityEvent {

    @NotBlank
    private String eventId;

    @NotNull
    private Instant timestamp;

    @NotNull
    private Integer configId;

    private String policyId;

    @NotBlank
    private String clientIp;

    private String hostname;

    @NotBlank
    private String path;

    private String method;
    
    @Min(100)
    @Max(599)
    private Integer statusCode;
    private String userAgent;

    @Valid
    @NotNull
    private SecurityRule rule;

    @NotNull
    private Action action;
    private GeoLocation geoLocation;
    @PositiveOrZero
    private Long requestSize;
    @PositiveOrZero
    private Long responseSize;

    public SecurityEvent() {
    }

    public SecurityEvent(String eventId, Instant timestamp, Integer configId, String policyId, String clientIp,
            String hostname, String path, String method, Integer statusCode, String userAgent, SecurityRule rule,
            Action action, GeoLocation geoLocation, Long requestSize, Long responseSize) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.configId = configId;
        this.policyId = policyId;
        this.clientIp = clientIp;
        this.hostname = hostname;
        this.path = path;
        this.method = method;
        this.statusCode = statusCode;
        this.userAgent = userAgent;
        this.rule = rule;
        this.action = action;
        this.geoLocation = geoLocation;
        this.requestSize = requestSize;
        this.responseSize = responseSize;
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

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
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

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public SecurityRule getRule() {
        return rule;
    }

    public void setRule(SecurityRule rule) {
        this.rule = rule;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
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
}
