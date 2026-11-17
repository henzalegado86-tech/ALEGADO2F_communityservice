package com.scc.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityLog {
    private int id;
    private int userId;
    private String actionType;
    private String description;
    private LocalDateTime timestamp;

    public ActivityLog(int id, int userId, String actionType, String description, String timestampStr) {
        this.id = id;
        this.userId = userId;
        this.actionType = actionType;
        this.description = description;
        if (timestampStr != null && !timestampStr.isEmpty()) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                this.timestamp = LocalDateTime.parse(timestampStr, fmt);
            } catch (Exception e) {
                this.timestamp = LocalDateTime.now();
            }
        }
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getActionType() { return actionType; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
}