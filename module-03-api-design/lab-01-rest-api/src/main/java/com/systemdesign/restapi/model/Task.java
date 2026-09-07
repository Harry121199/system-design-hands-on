package com.systemdesign.restapi.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task(){}

    public Task(String title, String description, String priority) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.title = title;
        this.description = description;
        this.status = "TODO";
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
