package com.systemdesign.cachedapi.repository;

import com.systemdesign.cachedapi.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p")
    List<Project> findAllProjects();

    @Query("SELECT p FROM Project p JOIN FETCH p.tasks")
    List<Project> findAllWithTasks();
}
