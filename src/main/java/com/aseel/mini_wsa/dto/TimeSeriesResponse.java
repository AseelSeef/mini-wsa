package com.aseel.mini_wsa.dto;

import java.util.List;

public class TimeSeriesResponse {

    private Integer configId;
    private TimeRange timeRange;
    private String interval;
    private List<TimeBucket> buckets;

    
    public TimeSeriesResponse() {
    }


    public TimeSeriesResponse(Integer configId, TimeRange timeRange, String interval, List<TimeBucket> buckets) {
        this.configId = configId;
        this.timeRange = timeRange;
        this.interval = interval;
        this.buckets = buckets;
    }


    public Integer getConfigId() {
        return configId;
    }


    public void setConfigId(Integer configId) {
        this.configId = configId;
    }


    public TimeRange getTimeRange() {
        return timeRange;
    }


    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }


    public String getInterval() {
        return interval;
    }


    public void setInterval(String interval) {
        this.interval = interval;
    }


    public List<TimeBucket> getBuckets() {
        return buckets;
    }


    public void setBuckets(List<TimeBucket> buckets) {
        this.buckets = buckets;
    }

    
    

}
