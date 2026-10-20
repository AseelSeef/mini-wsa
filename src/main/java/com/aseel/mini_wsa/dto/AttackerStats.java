package com.aseel.mini_wsa.dto;

public class AttackerStats {

    private String clientIp;
    private long count;
    private double avgThreatScore;

    public AttackerStats() {
    }

    public AttackerStats(String clientIp, long count, double avgThreatScore) {
        this.clientIp = clientIp;
        this.count = count;
        this.avgThreatScore = avgThreatScore;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
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
