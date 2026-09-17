package com.systemdesign.shardrouter.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;
import java.util.Random;

@Component
public class ShardInitializer implements CommandLineRunner {

    private final ShardRouter shardRouter;
    private static final String[] STATUSES = {"TODO", "IN_PROGRESS", "DONE"};
    private static final String[] PRIORITIES = {"LOW", "MEDIUM", "HIGH"};

    public ShardInitializer(ShardRouter shardRouter) {
        this.shardRouter = shardRouter;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create tables on every shard
        for (Map.Entry<String, DataSource> entry : shardRouter.getAllShards().entrySet()) {
            try (Connection conn = entry.getValue().getConnection();
                 Statement stmt = conn.createStatement()) {
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
                System.out.println("Tables created on " + entry.getKey());
            }
        }

        // Route 100 projects to shards
        Random random = new Random();
        Map<String, Integer> projectCounts = new java.util.LinkedHashMap<>();
        Map<String, Integer> taskCounts = new java.util.LinkedHashMap<>();
        shardRouter.getAllShards().keySet().forEach(s -> {
            projectCounts.put(s, 0);
            taskCounts.put(s, 0);
        });

        for (long projectId = 1; projectId <= 100; projectId++) {
            String shardName = shardRouter.getShardName(projectId);
            DataSource ds = shardRouter.getShardForProject(projectId);

            try (Connection conn = ds.getConnection()) {
                // Insert project
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO projects (id, name, user_name) VALUES (?, ?, ?)");
                ps.setLong(1, projectId);
                ps.setString(2, "Project " + projectId);
                ps.setString(3, "User " + ((projectId - 1) / 10 + 1));
                ps.executeUpdate();
                projectCounts.merge(shardName, 1, Integer::sum);

                // Insert 500 tasks per project
                PreparedStatement taskPs = conn.prepareStatement(
                        "INSERT INTO tasks (title, status, priority, project_id) VALUES (?, ?, ?, ?)");
                for (int t = 1; t <= 500; t++) {
                    taskPs.setString(1, "Project " + projectId + " - Task " + t);
                    taskPs.setString(2, STATUSES[random.nextInt(STATUSES.length)]);
                    taskPs.setString(3, PRIORITIES[random.nextInt(PRIORITIES.length)]);
                    taskPs.setLong(4, projectId);
                    taskPs.addBatch();
                    if (t % 500 == 0) taskPs.executeBatch();
                }
                taskCounts.merge(shardName, 500, Integer::sum);
            }
        }

        System.out.println("\n=== Shard distribution ===");
        for (String shard : projectCounts.keySet()) {
            System.out.printf("  %s: %d projects, %,d tasks%n",
                    shard, projectCounts.get(shard), taskCounts.get(shard));
        }
        System.out.println("Total: 100 projects, 50,000 tasks\n");
    }
}