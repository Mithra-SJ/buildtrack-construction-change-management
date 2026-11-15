
package com.construction.change.service;

import com.construction.change.dto.response.ProjectBaselineResponse;
import java.util.List;

public interface BaselineService {
    ProjectBaselineResponse createInitial(Long projectId);
    List<ProjectBaselineResponse> findByProject(Long projectId);
}
