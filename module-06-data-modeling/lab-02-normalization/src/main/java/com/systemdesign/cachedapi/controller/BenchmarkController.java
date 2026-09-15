package com.systemdesign.cachedapi.controller;

import com.systemdesign.cachedapi.repository.DenormalizedTaskRepository;
import com.systemdesign.cachedapi.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/benchmark")
public class BenchmarkController {

    private final DenormalizedTaskRepository denormalizedTaskRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    public BenchmarkController(DenormalizedTaskRepository denormalizedTaskRepository,
                               UserRepository userRepository) {
        this.denormalizedTaskRepository = denormalizedTaskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/normalization")
    @Transactional
    public Map<String, Object> benchmark() {
        Map<String, Object> results = new LinkedHashMap<>();
        Long projectId = 1L;
        Long userId = 1L;

        // ===== NORMALIZED READ (3-table JOIN) =====
        String joinSql = """
            SELECT t.id, t.title, t.status, t.priority, p.name AS project_name, u.name AS user_name
            FROM tasks t
            JOIN projects p ON t.project_id = p.id
            JOIN users u ON p.user_id = u.id
            WHERE t.project_id = :pid
            """;

        long start = System.nanoTime();
        em.createNativeQuery(joinSql).setParameter("pid", projectId).getResultList();
        long normalizedReadMs = (System.nanoTime() - start) / 1_000_000;
        results.put("1_normalized_read_500_tasks_ms", normalizedReadMs);

        // ===== DENORMALIZED READ (single table) =====
        start = System.nanoTime();
        denormalizedTaskRepository.findByProjectId(projectId);
        long denormalizedReadMs = (System.nanoTime() - start) / 1_000_000;
        results.put("2_denormalized_read_500_tasks_ms", denormalizedReadMs);

        // ===== NORMALIZED WRITE (update 1 row) =====
        start = System.nanoTime();
        em.createNativeQuery("UPDATE users SET name = 'Updated User' WHERE id = :uid")
                .setParameter("uid", userId).executeUpdate();
        long normalizedWriteMs = (System.nanoTime() - start) / 1_000_000;
        results.put("3_normalized_write_1_row_ms", normalizedWriteMs);

        // ===== DENORMALIZED WRITE (update 5,000 rows) =====
        start = System.nanoTime();
        int rowsUpdated = denormalizedTaskRepository.updateUserName(userId, "Updated User");
        long denormalizedWriteMs = (System.nanoTime() - start) / 1_000_000;
        results.put("4_denormalized_write_rows_updated", rowsUpdated);
        results.put("4_denormalized_write_ms", denormalizedWriteMs);

        // ===== SUMMARY =====
        results.put("5_read_winner", normalizedReadMs > denormalizedReadMs ? "DENORMALIZED" : "NORMALIZED");
        results.put("5_write_winner", normalizedWriteMs < denormalizedWriteMs ? "NORMALIZED" : "DENORMALIZED");

        return results;
    }
}