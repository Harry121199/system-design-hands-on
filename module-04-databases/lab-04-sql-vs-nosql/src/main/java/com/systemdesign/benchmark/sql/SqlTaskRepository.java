package com.systemdesign.benchmark.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SqlTaskRepository extends JpaRepository<SqlTask, Long> {
    List<SqlTask> findByStatus(String status);
    List<SqlTask> findByStatusAndPriority(String status, String priority);
}