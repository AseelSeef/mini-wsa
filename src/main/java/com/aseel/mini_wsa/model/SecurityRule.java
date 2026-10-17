package com.aseel.mini_wsa.model;

import jakarta.validation.constraints.NotNull;

public class SecurityRule {
    
    private String id;
    private String name;
    private String message;
    @NotNull
    private Severity severity;
    @NotNull
    private AttackCategory category;

    public SecurityRule() {
    }

    public SecurityRule(String id, String name, String message, Severity severity, AttackCategory category) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.severity = severity;
        this.category = category;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Severity getSeverity() {
        return severity;
    }
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
    public AttackCategory getCategory() {
        return category;
    }
    public void setCategory(AttackCategory category) {
        this.category = category;
    }

}
