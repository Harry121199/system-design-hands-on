package com.systemdesign.restapi.controller;

import com.systemdesign.restapi.model.Task;
import com.systemdesign.restapi.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Map<String, String> body) {
        Task task = taskService.create(
                body.get("title"),
                body.get("description"),
                body.getOrDefault("priority", "MEDIUM")
        );
        return ResponseEntity
                .created(URI.create("/api/v1/tasks/" + task.getId()))
                .body(task);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        List<Task> tasks = taskService.getAll(status, priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable String id) {
        return taskService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable String id,
                                       @RequestBody Map<String, String> body) {
        return taskService.update(id,
                        body.get("title"),
                        body.get("description"),
                        body.get("status"),
                        body.get("priority"))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (taskService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}