package com.systemdesign.cachedapi.repository;

import com.systemdesign.cachedapi.model.DenormalizedTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DenormalizedTaskRepository extends JpaRepository<DenormalizedTask, Long> {

    List<DenormalizedTask> findByProjectId(Long projectId);

    @Modifying
    @Query("UPDATE DenormalizedTask d SET d.userName = :name WHERE d.userId = :userId")
    int updateUserName(@Param("userId") Long userId, @Param("name") String name);
}