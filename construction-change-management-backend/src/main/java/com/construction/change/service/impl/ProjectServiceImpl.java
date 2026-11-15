package com.construction.change.service.impl;

import com.construction.change.dto.request.CreateProjectRequest;
import com.construction.change.dto.response.ProjectResponse;
import com.construction.change.entity.Project;
import com.construction.change.exception.BadRequestException;
import com.construction.change.exception.ResourceNotFoundException;
import com.construction.change.repository.ProjectRepository;
import com.construction.change.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    public ProjectServiceImpl(ProjectRepository projectRepository) { this.projectRepository = projectRepository; }

    @Override
    public ProjectResponse create(CreateProjectRequest request) {
        if (request.plannedEndDate().isBefore(request.startDate()))
            throw new BadRequestException("Planned end date cannot be before start date");
        if (projectRepository.existsByProjectCode(request.projectCode()))
            throw new BadRequestException("Project code already exists");
        Project project = new Project(request.projectCode(), request.name(), request.budget(),
                request.startDate(), request.plannedEndDate());
        return ProjectResponse.from(projectRepository.save(project));
    }
    @Override @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream().map(ProjectResponse::from).toList();
    }
    @Override @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return ProjectResponse.from(projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id)));
    }
}
