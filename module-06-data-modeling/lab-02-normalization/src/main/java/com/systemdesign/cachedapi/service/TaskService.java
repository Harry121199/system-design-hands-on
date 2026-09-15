package com.systemdesign.cachedapi.service;

import com.systemdesign.cachedapi.model.Task;
import com.systemdesign.cachedapi.repository.TaskRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CacheManager cacheManager;

    private final AtomicInteger dbHits = new AtomicInteger(0);
    private final AtomicInteger cacheHits = new AtomicInteger(0);

    public TaskService(TaskRepository taskRepository, CacheManager cacheManager) {
        this.taskRepository = taskRepository;
        this.cacheManager = cacheManager;
    }

    // ============ STRATEGY 1: CACHE-ASIDE (lazy loading) ============
    // Read: check cache → miss? query DB → store in cache
    // Write: update DB → evict from cache
    // Next read will miss and reload fresh data

    @Cacheable(value = "taskById", key = "#id")
    public Optional<Task> getByIdCacheAside(Long id) {
        dbHits.incrementAndGet();
        simulateSlowQuery();
        return taskRepository.findById(id);
    }

    @CacheEvict(value = "taskById", key = "#id")
    public Optional<Task> updateCacheAside(Long id, String title, String status, String priority) {
        return doUpdate(id, title, status, priority);
        // After this returns, Spring evicts the cache entry
        // Next getById() will miss cache → hit DB → store fresh data
    }

    // ============ STRATEGY 2: WRITE-THROUGH ============
    // Read: check cache → miss? query DB → store in cache
    // Write: update DB → immediately update cache too
    // No stale window — cache is always fresh

    public Optional<Task> getByIdWriteThrough(Long id) {
        Cache cache = cacheManager.getCache("writeThrough");
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(id);
            if (wrapper != null) {
                cacheHits.incrementAndGet();
                return Optional.ofNullable((Task) wrapper.get());
            }
        }

        dbHits.incrementAndGet();
        simulateSlowQuery();
        Optional<Task> task = taskRepository.findById(id);

        if (cache != null && task.isPresent()) {
            cache.put(id, task.get());
        }
        return task;
    }

    public Optional<Task> updateWriteThrough(Long id, String title, String status, String priority) {
        Optional<Task> updated = doUpdate(id, title, status, priority);

        // Immediately write the updated value into cache
        Cache cache = cacheManager.getCache("writeThrough");
        if (cache != null && updated.isPresent()) {
            cache.put(id, updated.get());
        }
        return updated;
    }

    // ============ STRATEGY 3: TTL-BASED (time to live) ============
    // Cache expires after a fixed time, regardless of updates
    // Simple but allows stale reads within the TTL window

    @Cacheable(value = "ttlCache", key = "#id")
    public Optional<Task> getByIdTTL(Long id) {
        dbHits.incrementAndGet();
        simulateSlowQuery();
        return taskRepository.findById(id);
    }

    // No eviction on write — cache just expires after TTL
    public Optional<Task> updateTTL(Long id, String title, String status, String priority) {
        return doUpdate(id, title, status, priority);
        // Cache NOT evicted — stale data served until TTL expires
    }

    // ============ NO CACHE (baseline) ============

    public Optional<Task> getByIdNoCache(Long id) {
        dbHits.incrementAndGet();
        simulateSlowQuery();
        return taskRepository.findById(id);
    }

    // ============ HELPERS ============

    private Optional<Task> doUpdate(Long id, String title, String status, String priority) {
        return taskRepository.findById(id).map(task -> {
            if (title != null) task.setTitle(title);
            if (status != null) task.setStatus(status);
            if (priority != null) task.setPriority(priority);
            return taskRepository.save(task);
        });
    }

    private void simulateSlowQuery() {
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
    }

    public void resetStats() {
        dbHits.set(0);
        cacheHits.set(0);
    }

    public int getDbHits() { return dbHits.get(); }
    public int getCacheHits() { return cacheHits.get(); }

    @CacheEvict(value = {"taskById", "tasksByStatus", "writeThrough", "ttlCache"}, allEntries = true)
    public void evictAll() {
        System.out.println("All caches evicted.");
    }
}