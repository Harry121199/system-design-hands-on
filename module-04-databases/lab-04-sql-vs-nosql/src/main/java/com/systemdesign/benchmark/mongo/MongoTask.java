package com.systemdesign.benchmark.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document("tasks")
public class MongoTask {

    @Id
    private String id;

    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime createdAt;

    private List<String> tags;
    private Map<String, String> metadata;

    public MongoTask() {}

    public MongoTask(String title, String description, String status, String priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }

    public void setTags(List<String> tags) { this.tags = tags; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
}