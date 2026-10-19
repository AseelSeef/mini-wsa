package com.aseel.mini_wsa.dto;

import java.util.List;

public class IngestionResponse {

    private int created;
    private int existing;
    private int rejected;
    private List<EventIngestionResult> results;

    public IngestionResponse(int created, int existing, int rejected, List<EventIngestionResult> results) {
        this.created = created;
        this.existing = existing;
        this.rejected = rejected;
        this.results = results;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getExisting() {
        return existing;
    }

    public void setExisting(int existing) {
        this.existing = existing;
    }

    public int getRejected() {
        return rejected;
    }

    public void setRejected(int rejected) {
        this.rejected = rejected;
    }

    public List<EventIngestionResult> getResults() {
        return results;
    }

    public void setResults(List<EventIngestionResult> results) {
        this.results = results;
    }
    

}
