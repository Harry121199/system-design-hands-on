package com.systemdesign.nosql.repository;


import com.systemdesign.nosql.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByStatus(String status);

    List<Task> findByPriority(String priority);

    List<Task> findByStatusAndPriority(String status, String priority);

    List<Task> findByTagsContaining(String tags);

    List<Task> findByTitleContainingIgnoreCase(String keyword);
}
