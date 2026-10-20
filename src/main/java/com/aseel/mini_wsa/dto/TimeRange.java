package com.aseel.mini_wsa.dto;

import java.time.Instant;

public class TimeRange {

    private Instant from;
    private Instant to;

    public TimeRange() {
    }

    public TimeRange(Instant from, Instant to) {
        this.from = from;
        this.to = to;
    }

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public Instant getTo() {
        return to;
    }

    public void setTo(Instant to) {
        this.to = to;
    }   

    

}
