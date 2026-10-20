package com.aseel.mini_wsa.dto;

public class CategoryStats {

    private long count;
    private double avgThreatScore;

    public CategoryStats() {
    }

    public CategoryStats(long count, double avgThreatScore) {
        this.count = count;
        this.avgThreatScore = avgThreatScore;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getAvgThreatScore() {
        return avgThreatScore;
    }

    public void setAvgThreatScore(double avgThreatScore) {
        this.avgThreatScore = avgThreatScore;
    }

}
