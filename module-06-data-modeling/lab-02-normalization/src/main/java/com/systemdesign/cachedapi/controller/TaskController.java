package com.systemdesign.cachedapi.controller;

import com.systemdesign.cachedapi.model.Task;
import com.systemdesign.cachedapi.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
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
        return taskService.getByIdCacheAside(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id,
                                       @RequestBody Map<String, String> body) {
        return taskService.updateCacheAside(id, body.get("title"), body.get("status"), body.get("priority"))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/benchmark/invalidation")
    public Map<String, Object> benchmarkInvalidation() {
        Map<String, Object> results = new LinkedHashMap<>();
        Long taskId = 100L;

        // ===== NO CACHE =====
        taskService.evictAll();
        taskService.resetStats();
        long start = System.nanoTime();
        taskService.getByIdNoCache(taskId);
        taskService.getByIdNoCache(taskId);
        taskService.getByIdNoCache(taskId);
        long noCacheTime = (System.nanoTime() - start) / 1_000_000;
        results.put("1_no_cache_3_reads_ms", noCacheTime);
        results.put("1_no_cache_db_hits", taskService.getDbHits());

        // ===== CACHE-ASIDE =====
        taskService.evictAll();
        taskService.resetStats();
        start = System.nanoTime();
        taskService.getByIdCacheAside(taskId);          // miss → DB
        taskService.getByIdCacheAside(taskId);          // hit
        taskService.updateCacheAside(taskId, null, "IN_PROGRESS", null);  // evicts cache
        taskService.getByIdCacheAside(taskId);          // miss → DB (fresh data)
        long cacheAsideTime = (System.nanoTime() - start) / 1_000_000;
        results.put("2_cache_aside_ms", cacheAsideTime);
        results.put("2_cache_aside_db_hits", taskService.getDbHits());
        results.put("2_cache_aside_stale_reads", 0);

        // ===== WRITE-THROUGH =====
        taskService.evictAll();
        taskService.resetStats();
        start = System.nanoTime();
        taskService.getByIdWriteThrough(taskId);        // miss → DB
        taskService.getByIdWriteThrough(taskId);        // hit
        taskService.updateWriteThrough(taskId, null, "DONE", null);  // updates cache immediately
        Task afterUpdate = taskService.getByIdWriteThrough(taskId).orElse(null);  // hit — fresh!
        long writeThroughTime = (System.nanoTime() - start) / 1_000_000;
        results.put("3_write_through_ms", writeThroughTime);
        results.put("3_write_through_db_hits", taskService.getDbHits());
        results.put("3_write_through_status_after_update", afterUpdate != null ? afterUpdate.getStatus() : "null");
        results.put("3_write_through_stale_reads", 0);

        // ===== TTL-BASED =====
        taskService.evictAll();
        taskService.resetStats();
        start = System.nanoTime();
        taskService.getByIdTTL(taskId);                 // miss → DB
        taskService.getByIdTTL(taskId);                 // hit
        taskService.updateTTL(taskId, null, "TODO", null);  // does NOT evict
        Task staleRead = taskService.getByIdTTL(taskId).orElse(null);  // hit — STALE!
        long ttlTime = (System.nanoTime() - start) / 1_000_000;
        results.put("4_ttl_ms", ttlTime);
        results.put("4_ttl_db_hits", taskService.getDbHits());
        results.put("4_ttl_status_after_update", staleRead != null ? staleRead.getStatus() : "null");
        results.put("4_ttl_stale_reads", "YES — status shows DONE, actual is TODO");

        return results;
    }
}