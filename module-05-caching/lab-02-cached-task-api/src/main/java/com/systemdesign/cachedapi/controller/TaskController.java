package com.systemdesign.cachedapi.controller;

import com.systemdesign.cachedapi.model.Task;
import com.systemdesign.cachedapi.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getById(@PathVariable Long id) {
        return taskService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tasks")
    public List<Task> getByStatus(@RequestParam String status) {
        return taskService.getByStatus(status);
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id,
                                       @RequestBody Map<String, String> body) {
        return taskService.update(id, body.get("title"), body.get("status"), body.get("priority"))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cache/evict")
    public String evictAll() {
        taskService.evictAll();
        return "All caches evicted.";
    }

    @GetMapping("/benchmark")
    public Map<String, Object> benchmark() {
        Map<String, Object> results = new LinkedHashMap<>();

        // Without cache
        long start1 = System.nanoTime();
        taskService.getByStatusNoCache("TODO");
        long noCacheTime = (System.nanoTime() - start1) / 1_000_000;

        // First call with cache (cold — misses, hits DB)
        taskService.evictAll();
        long start2 = System.nanoTime();
        taskService.getByStatus("TODO");
        long coldCacheTime = (System.nanoTime() - start2) / 1_000_000;

        // Second call with cache (warm — hits cache)
        long start3 = System.nanoTime();
        taskService.getByStatus("TODO");
        long warmCacheTime = (System.nanoTime() - start3) / 1_000_000;

        results.put("no_cache_ms", noCacheTime);
        results.put("cold_cache_ms", coldCacheTime);
        results.put("warm_cache_ms", warmCacheTime);
        results.put("speedup", noCacheTime + "ms → " + warmCacheTime + "ms");

        return results;
    }
}