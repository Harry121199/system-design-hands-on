package com.systemdesign.benchmark.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MongoTaskRepository extends MongoRepository<MongoTask, String> {
    List<MongoTask> findByStatus(String status);
    List<MongoTask> findByStatusAndPriority(String status, String priority);
}