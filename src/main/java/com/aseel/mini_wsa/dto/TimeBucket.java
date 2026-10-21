package com.aseel.mini_wsa.dto;

import java.time.Instant;

public class TimeBucket {

    private Instant timestamp;
    private long count;

    public TimeBucket() {
    }

    public TimeBucket(Instant timestamp, long count) {
        this.timestamp = timestamp;
        this.count = count;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    public long getCount() {
        return count;
    }
    public void setCount(long count) {
        this.count = count;
    }

    

}
