package com.systemdesign.benchmark.controller;

import com.systemdesign.benchmark.sql.SqlTask;
import com.systemdesign.benchmark.sql.SqlTaskRepository;
import com.systemdesign.benchmark.mongo.MongoTask;
import com.systemdesign.benchmark.mongo.MongoTaskRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/benchmark")
public class BenchmarkController {

    private final SqlTaskRepository sqlRepo;
    private final MongoTaskRepository mongoRepo;

    private static final String[] STATUSES = {"TODO", "IN_PROGRESS", "DONE"};
    private static final String[] PRIORITIES = {"LOW", "MEDIUM", "HIGH"};
    private static final int BATCH_SIZE = 1000;

    public BenchmarkController(SqlTaskRepository sqlRepo, MongoTaskRepository mongoRepo) {
        this.sqlRepo = sqlRepo;
        this.mongoRepo = mongoRepo;
    }

    @GetMapping("/run")
    public Map<String, Object> runBenchmark(@RequestParam(defaultValue = "5000") int count) {
        Map<String, Object> results = new LinkedHashMap<>();
        results.put("entries", count);

        // Clean up
        sqlRepo.deleteAll();
        mongoRepo.deleteAll();

        Random random = new Random(42);

        // ===== INSERT BENCHMARK =====

        // SQL inserts
        long sqlInsertStart = System.nanoTime();
        List<SqlTask> sqlBatch = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            sqlBatch.add(new SqlTask(
                    "Task " + i,
                    "Description for task " + i,
                    STATUSES[random.nextInt(3)],
                    PRIORITIES[random.nextInt(3)]
            ));
            if (sqlBatch.size() == BATCH_SIZE) {
                sqlRepo.saveAll(sqlBatch);
                sqlBatch.clear();
            }
        }
        if (!sqlBatch.isEmpty()) sqlRepo.saveAll(sqlBatch);
        long sqlInsertTime = (System.nanoTime() - sqlInsertStart) / 1_000_000;

        // MongoDB inserts
        random = new Random(42);
        long mongoInsertStart = System.nanoTime();
        List<MongoTask> mongoBatch = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            MongoTask task = new MongoTask(
                    "Task " + i,
                    "Description for task " + i,
                    STATUSES[random.nextInt(3)],
                    PRIORITIES[random.nextInt(3)]
            );
            task.setTags(List.of("tag" + (i % 10), "batch" + (i / 1000)));
            task.setMetadata(Map.of("source", "benchmark", "index", String.valueOf(i)));
            mongoBatch.add(task);
            if (mongoBatch.size() == BATCH_SIZE) {
                mongoRepo.saveAll(mongoBatch);
                mongoBatch.clear();
            }
        }
        if (!mongoBatch.isEmpty()) mongoRepo.saveAll(mongoBatch);
        long mongoInsertTime = (System.nanoTime() - mongoInsertStart) / 1_000_000;

        results.put("insert_sql_ms", sqlInsertTime);
        results.put("insert_mongo_ms", mongoInsertTime);

        // ===== QUERY BENCHMARK =====

        // Single field query
        long sqlQueryStart = System.nanoTime();
        List<SqlTask> sqlResults = sqlRepo.findByStatus("TODO");
        long sqlQueryTime = (System.nanoTime() - sqlQueryStart) / 1_000_000;

        long mongoQueryStart = System.nanoTime();
        List<MongoTask> mongoResults = mongoRepo.findByStatus("TODO");
        long mongoQueryTime = (System.nanoTime() - mongoQueryStart) / 1_000_000;

        results.put("query_single_sql_ms", sqlQueryTime);
        results.put("query_single_sql_rows", sqlResults.size());
        results.put("query_single_mongo_ms", mongoQueryTime);
        results.put("query_single_mongo_docs", mongoResults.size());

        // Compound query
        long sqlCompStart = System.nanoTime();
        List<SqlTask> sqlComp = sqlRepo.findByStatusAndPriority("TODO", "HIGH");
        long sqlCompTime = (System.nanoTime() - sqlCompStart) / 1_000_000;

        long mongoCompStart = System.nanoTime();
        List<MongoTask> mongoComp = mongoRepo.findByStatusAndPriority("TODO", "HIGH");
        long mongoCompTime = (System.nanoTime() - mongoCompStart) / 1_000_000;

        results.put("query_compound_sql_ms", sqlCompTime);
        results.put("query_compound_sql_rows", sqlComp.size());
        results.put("query_compound_mongo_ms", mongoCompTime);
        results.put("query_compound_mongo_docs", mongoComp.size());

        // ===== READ ALL =====
        long sqlReadStart = System.nanoTime();
        long sqlCount = sqlRepo.count();
        long sqlReadTime = (System.nanoTime() - sqlReadStart) / 1_000_000;

        long mongoReadStart = System.nanoTime();
        long mongoCount = mongoRepo.count();
        long mongoReadTime = (System.nanoTime() - mongoReadStart) / 1_000_000;

        results.put("count_sql_ms", sqlReadTime);
        results.put("count_sql_total", sqlCount);
        results.put("count_mongo_ms", mongoReadTime);
        results.put("count_mongo_total", mongoCount);

        // ===== FLEXIBILITY TEST =====
        results.put("mongo_extra_fields", "tags + metadata embedded in each doc — no schema change needed");
        results.put("sql_extra_fields", "would require ALTER TABLE or new join tables");

        return results;
    }
}