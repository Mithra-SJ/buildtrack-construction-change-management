package com.construction.change.repository;

import com.construction.change.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByProjectCode(String projectCode);
}
