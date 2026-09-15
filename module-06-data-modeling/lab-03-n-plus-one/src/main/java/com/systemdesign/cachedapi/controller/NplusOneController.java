package com.systemdesign.cachedapi.controller;

import com.systemdesign.cachedapi.config.QueryCounter;
import com.systemdesign.cachedapi.model.Project;
import com.systemdesign.cachedapi.repository.ProjectRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/benchmark")
public class NplusOneController {

    private final ProjectRepository projectRepository;

    public NplusOneController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping("/n-plus-one")
    public Map<String, Object> benchmark() {
        Map<String, Object> results = new LinkedHashMap<>();

        // ===== N+1 PROBLEM =====
        QueryCounter.reset();
        long start = System.nanoTime();

        List<Project> projects = projectRepository.findAllProjects();
        int totalTasks = 0;
        for (Project p : projects) {
            totalTasks += p.getTasks().size();  // Each call fires a SELECT
        }

        long nPlusOneMs = (System.nanoTime() - start) / 1_000_000;
        int nPlusOneQueries = QueryCounter.getCount();

        results.put("1_n_plus_one_queries", nPlusOneQueries);
        results.put("1_n_plus_one_ms", nPlusOneMs);
        results.put("1_n_plus_one_tasks_loaded", totalTasks);

        // ===== JOIN FETCH FIX =====
        QueryCounter.reset();
        start = System.nanoTime();

        List<Project> projectsWithTasks = projectRepository.findAllWithTasks();
        int totalTasks2 = 0;
        for (Project p : projectsWithTasks) {
            totalTasks2 += p.getTasks().size();  // Already in memory — no query
        }

        long joinFetchMs = (System.nanoTime() - start) / 1_000_000;
        int joinFetchQueries = QueryCounter.getCount();

        results.put("2_join_fetch_queries", joinFetchQueries);
        results.put("2_join_fetch_ms", joinFetchMs);
        results.put("2_join_fetch_tasks_loaded", totalTasks2);

        // ===== SUMMARY =====
        results.put("3_queries_saved", nPlusOneQueries - joinFetchQueries);
        results.put("3_speedup", nPlusOneMs > 0 ? joinFetchMs > 0 ? nPlusOneMs + "ms → " + joinFetchMs + "ms" : nPlusOneMs + "ms → <1ms" : "both fast");

        return results;
    }
}