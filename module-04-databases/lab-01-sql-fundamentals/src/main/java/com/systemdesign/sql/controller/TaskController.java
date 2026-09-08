package com.systemdesign.sql.controller;


import com.systemdesign.sql.model.Task;
import com.systemdesign.sql.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    public ResponseEntity<Task> create(@RequestBody Map<String, String> body){
        Task task = new Task(
          body.get("title"),
          body.get("description"),
          body.getOrDefault("priority","MEDIUM")
        );
        Task saved = taskRepository.save(task);
        return ResponseEntity
                .created(URI.create("/api/v1/tasks/"+saved.getId()))
                .body(saved);
    }

    @GetMapping
    public List<Task> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        if(status != null && priority != null){
            return taskRepository.findByStatusAndPriority(status,priority);
        }else if (status != null) {
            return taskRepository.findByStatus(status);
        } else if (priority != null) {
            return taskRepository.findByPriority(priority);
        }
        return taskRepository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Task> search(@RequestParam String keyword) {
        return taskRepository.searchByKeyword(keyword);
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return Map.of(
                "total", taskRepository.count(),
                "todo", taskRepository.countByStatus("TODO"),
                "in_progress", taskRepository.countByStatus("IN_PROGRESS"),
                "done", taskRepository.countByStatus("DONE")
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id,
                                       @RequestBody Map<String, String> body) {
        return taskRepository.findById(id)
                .map(task -> {
                    if (body.containsKey("title")) task.setTitle(body.get("title"));
                    if (body.containsKey("description")) task.setDescription(body.get("description"));
                    if (body.containsKey("status")) task.setStatus(body.get("status"));
                    if (body.containsKey("priority")) task.setPriority(body.get("priority"));
                    return ResponseEntity.ok(taskRepository.save(task));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}