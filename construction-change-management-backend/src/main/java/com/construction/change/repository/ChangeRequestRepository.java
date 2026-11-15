package com.construction.change.repository;

import com.construction.change.entity.ChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, Long> {
    List<ChangeRequest> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}
