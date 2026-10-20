package com.aseel.mini_wsa.dto;

import java.util.List;
import java.util.Map;

import com.aseel.mini_wsa.model.Action;
import com.aseel.mini_wsa.model.AttackCategory;

public class SummaryResponse {

    private Integer configId;
    private TimeRange timeRange;
    private long totalEvents;
    private Map<AttackCategory, CategoryStats> byCategory;
    private Map<Action, Long> byAction;
    private List<AttackerStats> topAttackers;
    private List<TargetedPathStats> topTargetedPaths;

    public SummaryResponse() {
    }

    public SummaryResponse(Integer configId, TimeRange timeRange, long totalEvents, Map<AttackCategory, CategoryStats> byCategory, Map<Action, Long> byAction, List<AttackerStats> topAttackers, List<TargetedPathStats> topTargetedPaths) {
        this.configId = configId;
        this.timeRange = timeRange;
        this.totalEvents = totalEvents;
        this.byCategory = byCategory;
        this.byAction = byAction;
        this.topAttackers = topAttackers;
        this.topTargetedPaths = topTargetedPaths;
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

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public Map<AttackCategory, CategoryStats> getByCategory() {
        return byCategory;
    }

    public void setByCategory(Map<AttackCategory, CategoryStats> byCategory) {
        this.byCategory = byCategory;
    }

    public Map<Action, Long> getByAction() {
        return byAction;
    }

    public void setByAction(Map<Action, Long> byAction) {
        this.byAction = byAction;
    }

    public List<AttackerStats> getTopAttackers() {
        return topAttackers;
    }

    public void setTopAttackers(List<AttackerStats> topAttackers) {
        this.topAttackers = topAttackers;
    }

    public List<TargetedPathStats> getTopTargetedPaths() {
        return topTargetedPaths;
    }

    public void setTopTargetedPaths(List<TargetedPathStats> topTargetedPaths) {
        this.topTargetedPaths = topTargetedPaths;
    }

    
}