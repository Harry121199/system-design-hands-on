package com.systemdesign.sql.repository;

import com.systemdesign.sql.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(String status);

    List<Task> findByPriority(String priority);

    List<Task> findByStatusAndPriority(String status, String priority);

    @Query("SELECT t FROM Task t WHERE t.title LIKE %:keyword% OR t.description LIKE %:keyword%")
    List<Task> searchByKeyword(String keyword);

    long countByStatus(String status);
}
