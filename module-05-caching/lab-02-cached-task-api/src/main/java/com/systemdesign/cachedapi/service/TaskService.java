package com.systemdesign.cachedapi.service;

import com.systemdesign.cachedapi.model.Task;
import com.systemdesign.cachedapi.repository.TaskRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Cacheable(value = "taskById", key = "#id")
    public Optional<Task> getById(Long id) {
        simulateSlowQuery();
        return taskRepository.findById(id);
    }

    @Cacheable(value = "taskByStatus", key = "#status")
    public List<Task> getByStatus(String status) {
        simulateSlowQuery();
        return taskRepository.findByStatus(status);
    }

    @Cacheable(value = "taskById", key = "#id")
    public Optional<Task> update(Long id, String title, String status, String priority) {
        return taskRepository.findById(id).map(task -> {
            if (title != null) task.setTitle(title);
            if (status != null) task.setStatus(status);
            if (priority != null) task.setPriority(priority);
            return taskRepository.save(task);
        });
    }

    @CacheEvict(value = {"taskById", "tasksByStatus"}, allEntries = true)
    public void evictAll() {
        System.out.println("All caches evicted");
    }

    public List<Task> getByStatusNoCache(String status) {
        simulateSlowQuery();
        return taskRepository.findByStatus(status);
    }

    private void simulateSlowQuery() {
        try {
            Thread.sleep(200);
        }catch (InterruptedException ignored){}
    }
}
