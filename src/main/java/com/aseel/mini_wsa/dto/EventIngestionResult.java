package com.aseel.mini_wsa.dto;

import java.util.List;

public class EventIngestionResult {

    private int index;
    private String eventId;
    private int status;
    private List<ValidationError> validationErrors;
    private EnrichedSecurityEvent event;

    public EventIngestionResult(int index, String eventId, int status, List<ValidationError> validationErrors) {
        this.index = index;
        this.eventId = eventId;
        this.status = status;
        this.validationErrors = validationErrors;
    }

    public EnrichedSecurityEvent getEvent() {
        return event;
    }

    public void setEvent(EnrichedSecurityEvent event) {
        this.event = event;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    

}
