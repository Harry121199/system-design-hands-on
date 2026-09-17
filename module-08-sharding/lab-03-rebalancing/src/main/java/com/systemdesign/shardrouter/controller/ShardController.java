package com.systemdesign.shardrouter.controller;

import com.systemdesign.shardrouter.config.ShardRouter;
import com.systemdesign.shardrouter.service.RebalanceService;
import com.systemdesign.shardrouter.service.ScatterGatherService;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class ShardController {

    private final ShardRouter shardRouter;
    private final ScatterGatherService scatterGatherService;
    private final RebalanceService rebalanceService;

    public ShardController(ShardRouter shardRouter, ScatterGatherService scatterGatherService,  RebalanceService rebalanceService) {
        this.shardRouter = shardRouter;
        this.scatterGatherService = scatterGatherService;
        this.rebalanceService = rebalanceService;
    }

    @PostMapping("/rebalance/{shardName}")
    public Map<String, Object> rebalance(@PathVariable String shardName) throws SQLException {
        return rebalanceService.rebalance(shardName);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public Map<String, Object> getTasksByProject(@PathVariable Long projectId) throws SQLException {
        String shardName = shardRouter.getShardName(projectId);
        DataSource ds = shardRouter.getShardForProject(projectId);
        long start = System.nanoTime();

        List<Map<String, Object>> tasks = new ArrayList<>();
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, title, status, priority FROM tasks WHERE project_id = ?")) {
            ps.setLong(1, projectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> task = new LinkedHashMap<>();
                task.put("id", rs.getLong("id"));
                task.put("title", rs.getString("title"));
                task.put("status", rs.getString("status"));
                task.put("priority", rs.getString("priority"));
                tasks.add(task);
            }
        }

        long ms = (System.nanoTime() - start) / 1_000_000;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("shard", shardName);
        result.put("project_id", projectId);
        result.put("task_count", tasks.size());
        result.put("query_time_ms", ms);
        result.put("tasks", tasks.subList(0, Math.min(3, tasks.size())));
        return result;
    }

    @GetMapping("/tasks/by-priority/{priority}")
    public Map<String, Object> getTasksByPriority(@PathVariable String priority) {
        return scatterGatherService.queryAllShards(
                "SELECT id, title, status, priority, project_id FROM tasks WHERE priority = ?",
                priority.toUpperCase()
        );
    }

    @GetMapping("/tasks/by-status/{status}")
    public Map<String, Object> getTasksByStatus(@PathVariable String status) {
        return scatterGatherService.queryAllShards(
                "SELECT id, title, status, priority, project_id FROM tasks WHERE status = ?",
                status.toUpperCase()
        );
    }

    @GetMapping("/tasks/count-by-status")
    public Map<String, Object> countByStatus() {
        return scatterGatherService.queryAllShards(
                "SELECT status, COUNT(*) AS count FROM tasks GROUP BY status",
                null
        );
    }

    @GetMapping("/benchmark/single-vs-scatter")
    public Map<String, Object> benchmark() throws SQLException {
        Map<String, Object> results = new LinkedHashMap<>();

        // Single-shard query (knows the project)
        long start = System.nanoTime();
        DataSource ds = shardRouter.getShardForProject(1L);
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) FROM tasks WHERE project_id = ?")) {
            ps.setLong(1, 1L);
            ps.executeQuery();
        }
        long singleMs = (System.nanoTime() - start) / 1_000_000;
        results.put("1_single_shard_query_ms", singleMs);

        // Cross-shard scatter-gather
        start = System.nanoTime();
        scatterGatherService.queryAllShards(
                "SELECT COUNT(*) AS count FROM tasks WHERE priority = ?", "HIGH");
        long scatterMs = (System.nanoTime() - start) / 1_000_000;
        results.put("2_scatter_gather_3_shards_ms", scatterMs);

        results.put("3_overhead", scatterMs > singleMs
                ? scatterMs + "ms vs " + singleMs + "ms"
                : "scatter was faster (parallel execution)");

        return results;
    }

    @GetMapping("/shard-map")
    public Map<String, Object> getShardMap() throws SQLException {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, DataSource> entry : shardRouter.getAllShards().entrySet()) {
            String shardName = entry.getKey();
            try (Connection conn = entry.getValue().getConnection();
                 Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM projects");
                rs.next();
                int projects = rs.getInt(1);
                rs = stmt.executeQuery("SELECT COUNT(*) FROM tasks");
                rs.next();
                int tasks = rs.getInt(1);
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("projects", projects);
                info.put("tasks", tasks);
                result.put(shardName, info);
            }
        }
        return result;
    }
}