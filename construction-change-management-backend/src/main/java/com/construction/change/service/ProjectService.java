package com.construction.change.service;

import com.construction.change.dto.request.CreateProjectRequest;
import com.construction.change.dto.response.ProjectResponse;
import java.util.List;

public interface ProjectService {
    ProjectResponse create(CreateProjectRequest request);
    List<ProjectResponse> findAll();
    ProjectResponse findById(Long id);
}
