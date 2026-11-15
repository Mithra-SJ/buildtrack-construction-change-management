package com.construction.change.repository;

import com.construction.change.entity.ProjectBaseline;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BaselineRepository extends JpaRepository<ProjectBaseline, Long> {
    List<ProjectBaseline> findByProjectIdOrderByVersionDesc(Long projectId);
}
