package com.systemdesign.cachedapi.repository;

import com.systemdesign.cachedapi.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project,Long> {
}
