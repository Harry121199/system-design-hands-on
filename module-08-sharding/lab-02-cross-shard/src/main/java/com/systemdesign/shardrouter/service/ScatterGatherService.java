package com.systemdesign.shardrouter.service;

import com.systemdesign.shardrouter.config.ShardRouter;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

@Service
public class ScatterGatherService {

    private final ShardRouter shardRouter;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public ScatterGatherService(ShardRouter shardRouter) {
        this.shardRouter = shardRouter;
    }

    public Map<String, Object> queryAllShards(String sql, String paramValue) {
        Map<String, DataSource> shards = shardRouter.getAllShards();
        long start = System.nanoTime();

        // Scatter — send query to all shards in parallel
        Map<String, Future<List<Map<String, Object>>>> futures = new LinkedHashMap<>();
        for (Map.Entry<String, DataSource> entry : shards.entrySet()) {
            String shardName = entry.getKey();
            DataSource ds = entry.getValue();
            futures.put(shardName, executor.submit(() -> queryOneShard(ds, sql, paramValue)));
        }

        // Gather — collect results from all shards
        List<Map<String, Object>> merged = new ArrayList<>();
        Map<String, Object> perShard = new LinkedHashMap<>();

        for (Map.Entry<String, Future<List<Map<String, Object>>>> entry : futures.entrySet()) {
            String shardName = entry.getKey();
            try {
                List<Map<String, Object>> rows = entry.getValue().get(5, TimeUnit.SECONDS);
                merged.addAll(rows);
                perShard.put(shardName, rows.size());
            } catch (Exception e) {
                perShard.put(shardName, "ERROR: " + e.getMessage());
            }
        }

        long ms = (System.nanoTime() - start) / 1_000_000;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total_results", merged.size());
        result.put("shards_queried", shards.size());
        result.put("per_shard_counts", perShard);
        result.put("query_time_ms", ms);
        result.put("results_preview", merged.subList(0, Math.min(5, merged.size())));
        return result;
    }

    private List<Map<String, Object>> queryOneShard(DataSource ds, String sql, String paramValue)
            throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (paramValue != null) {
                ps.setString(1, paramValue);
            }
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= cols; i++) {
                    row.put(meta.getColumnName(i).toLowerCase(), rs.getObject(i));
                }
                rows.add(row);
            }
        }
        return rows;
    }
}