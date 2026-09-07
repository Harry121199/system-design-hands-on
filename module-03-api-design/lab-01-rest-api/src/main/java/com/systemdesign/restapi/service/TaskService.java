package com.systemdesign.restapi.service;

import com.systemdesign.restapi.model.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    public Task create(String title, String description, String priority) {
        Task task = new Task(title, description, priority);
        tasks.put(task.getId(), task);
        return task;
    }

    public Optional<Task> getById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public List<Task> getAll(String status, String priority) {
        return tasks.values().stream()
                .filter(t -> status == null || t.getStatus().equalsIgnoreCase(status))
                .filter(t -> priority == null || t.getPriority().equalsIgnoreCase(priority))
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                .toList();
    }

    public Optional<Task> update(String id, String title, String description,
                                 String status, String priority) {
        return getById(id).map(task -> {
            if (title != null) task.setTitle(title);
            if (description != null) task.setDescription(description);
            if (status != null) task.setStatus(status);
            if (priority != null) task.setPriority(priority);
            task.setUpdatedAt(LocalDateTime.now());
            return task;
        });
    }

    public boolean delete(String id) {
        return tasks.remove(id) != null;
    }
}