package com.scc.model;

public class ServiceRequest {
    private int id;
    private int residentId;
    private int categoryId;
    private String description;
    private String status;
    private String createdAt;

    public ServiceRequest(int id, int residentId, int categoryId, String description, String status, String createdAt) {
        this.id = id;
        this.residentId = residentId;
        this.categoryId = categoryId;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getResidentId() { return residentId; }
    public int getCategoryId() { return categoryId; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}