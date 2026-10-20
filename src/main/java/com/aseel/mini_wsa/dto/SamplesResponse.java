package com.aseel.mini_wsa.dto;

import java.util.List;

public class SamplesResponse {

    private List<EnrichedSecurityEvent> events;
    private long total;
    private int limit;
    private int offset;

    
    public SamplesResponse() {
    }

    public SamplesResponse(List<EnrichedSecurityEvent> events, long total, int limit, int offset) {
        this.events = events;
        this.total = total;
        this.limit = limit;
        this.offset = offset;
    }
    
    public List<EnrichedSecurityEvent> getEvents() {
        return events;
    }
    public void setEvents(List<EnrichedSecurityEvent> events) {
        this.events = events;
    }
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
    public int getOffset() {
        return offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }

    
}
