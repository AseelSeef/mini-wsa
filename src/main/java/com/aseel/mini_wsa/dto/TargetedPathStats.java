package com.aseel.mini_wsa.dto;

public class TargetedPathStats {

    private String path;
    private long count;

    public TargetedPathStats() {
    }

    public TargetedPathStats(String path, long count) {
        this.path = path;
        this.count = count;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
