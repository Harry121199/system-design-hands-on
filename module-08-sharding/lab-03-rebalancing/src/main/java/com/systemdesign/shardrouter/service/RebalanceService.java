package com.systemdesign.shardrouter.service;

import com.systemdesign.shardrouter.config.ShardRouter;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Service
public class RebalanceService {

    private final ShardRouter shardRouter;

    public RebalanceService(ShardRouter shardRouter) {
        this.shardRouter = shardRouter;
    }

    public Map<String,Object> rebalance(String newShardName) throws SQLException{
        Map<String, Object> result = new LinkedHashMap<>();
        long start = System.nanoTime();

        // Step 1 - Create tables on the new shard
        DataSource newDs = shardRouter.getDataSource(newShardName);
        try (Connection conn = newDs.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS projects (
                    id BIGINT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    user_name VARCHAR(255) NOT NULL
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tasks (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    status VARCHAR(20) NOT NULL,
                    priority VARCHAR(10) NOT NULL,
                    project_id BIGINT NOT NULL,
                    FOREIGN KEY (project_id) REFERENCES projects(id)
                )
            """);
        }
        System.out.println("[REBALANCE] Tables created on " + newShardName);

        // Step 2 - Snapshot current assignments (before adding to ring)
        Map<Long,String> beforeMap = new LinkedHashMap<>();
        for(long pid = 1; pid <= 100; pid++){
            beforeMap.put(pid,shardRouter.getShardName(pid));
        }

        // Step 3 - Add new shard to the hash ring
        shardRouter.addShardToRing(newShardName);

        // Step 4 - Detect which projects need to move
        List<Map<String,Object>> migrations = new ArrayList<>();
        for (long pid = 1; pid <= 100; pid++) {
            String oldShard = beforeMap.get(pid);
            String newShard = shardRouter.getShardName(pid);
            if(!oldShard.equals(newShard)){
                migrations.add(Map.of(
                   "project_id",pid,
                   "from",oldShard,
                   "to",newShard
                ));
            }
        }

        result.put("projects_to_migrate", migrations.size());
        result.put("migration_details", migrations);

        // Step 5 - Migrate data
        int tasksMigrated = 0;
        for(Map<String, Object> migration : migrations){
            long pid = (long)migration.get("project_id");
            String fromShard = (String)migration.get("from");
            String toShard = (String)migration.get("to");

            DataSource fromDs = shardRouter.getDataSource(fromShard);
            DataSource toDs = shardRouter.getDataSource(toShard);

            // Read projects from source
            try (Connection fromConn = fromDs.getConnection();
                 PreparedStatement ps = fromConn.prepareStatement(
                         "SELECT id, name, user_name FROM projects WHERE id = ?"
                 )) {
                ps.setLong(1, pid);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    // Write project to desitnation
                    try (Connection toConn = toDs.getConnection();
                         PreparedStatement insertPs = toConn.prepareStatement(
                                 "INSERT INTO projects (id, name, user_name) VALUES (?, ?, ?)")) {
                        insertPs.setLong(1, rs.getLong("id"));
                        insertPs.setString(2, rs.getString("name"));
                        insertPs.setString(3, rs.getString("user_name"));
                        insertPs.executeUpdate();
                    }
                }
            }

            // Read tasks from source
            try (Connection fromConn = fromDs.getConnection();
                 PreparedStatement ps = fromConn.prepareStatement(
                         "SELECT title, status, priority, project_id FROM tasks WHERE project_id = ?")) {
                ps.setLong(1, pid);
                ResultSet rs = ps.executeQuery();

                try (Connection toConn = toDs.getConnection();
                     PreparedStatement insertPs = toConn.prepareStatement(
                             "INSERT INTO tasks (title, status, priority, project_id) VALUES (?, ?, ?, ?)")) {
                    while (rs.next()) {
                        insertPs.setString(1, rs.getString("title"));
                        insertPs.setString(2, rs.getString("status"));
                        insertPs.setString(3, rs.getString("priority"));
                        insertPs.setLong(4, rs.getLong("project_id"));
                        insertPs.addBatch();
                        tasksMigrated++;
                    }
                    insertPs.executeBatch();
                }
            }

            // Delete from source
            try (Connection fromConn = fromDs.getConnection()) {
                PreparedStatement delTasks = fromConn.prepareStatement(
                        "DELETE FROM tasks WHERE project_id = ?");
                delTasks.setLong(1, pid);
                delTasks.executeUpdate();

                PreparedStatement delProject = fromConn.prepareStatement(
                        "DELETE FROM projects WHERE id = ?");
                delProject.setLong(1, pid);
                delProject.executeUpdate();
            }

            System.out.printf("[REBALANCE] Migrated project %d: %s → %s%n", pid, fromShard, toShard);
        }
        long ms = (System.nanoTime() - start) / 1_000_000;

        result.put("tasks_migrated", tasksMigrated);
        result.put("rebalance_time_ms", ms);
        result.put("percent_projects_moved", migrations.size() + "%");
        return result;
    }
}
