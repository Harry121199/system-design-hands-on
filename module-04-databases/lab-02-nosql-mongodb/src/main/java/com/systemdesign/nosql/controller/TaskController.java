package com.systemdesign.nosql.controller;

import com.systemdesign.nosql.model.Comment;
import com.systemdesign.nosql.model.Task;
import com.systemdesign.nosql.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping
    public Task create(@RequestBody Map<String, Object> body) {
        Task task = new Task(
                (String) body.get("title"),
                (String) body.get("description"),
                (String) body.getOrDefault("priority", "MEDIUM")
        );
        if (body.containsKey("tags")) {
            task.setTags((List<String>) body.get("tags"));
        }
        return taskRepository.save(task);
    }

    @GetMapping
    public List<Task> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String tag) {
        if (tag != null) {
            return taskRepository.findByTagsContaining(tag);
        } else if (status != null && priority != null) {
            return taskRepository.findByStatusAndPriority(status, priority);
        } else if (status != null) {
            return taskRepository.findByStatus(status);
        } else if (priority != null) {
            return taskRepository.findByPriority(priority);
        }
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable String id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Task> search(@RequestParam String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Task> addComment(@PathVariable String id,
                                           @RequestBody Map<String, String> body) {
        return taskRepository.findById(id)
                .map(task->{
                    Comment comment = new Comment(body.get("author"),body.get("text"));
                    task.getComments().add(comment);
                    return ResponseEntity.ok(taskRepository.save(task));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        if( taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
