
package com.construction.change.service.impl;

import com.construction.change.dto.response.ProjectBaselineResponse;
import com.construction.change.entity.Project;
import com.construction.change.entity.ProjectBaseline;
import com.construction.change.exception.ResourceNotFoundException;
import com.construction.change.repository.BaselineRepository;
import com.construction.change.repository.ProjectRepository;
import com.construction.change.service.BaselineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BaselineServiceImpl implements BaselineService {

    private final ProjectRepository projectRepository;
    private final BaselineRepository baselineRepository;

    public BaselineServiceImpl(
            ProjectRepository projectRepository,
            BaselineRepository baselineRepository) {
        this.projectRepository = projectRepository;
        this.baselineRepository = baselineRepository;
    }

    @Override
    public ProjectBaselineResponse createInitial(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found: " + projectId));

        List<ProjectBaseline> existing =
                baselineRepository.findByProjectIdOrderByVersionDesc(projectId);

        int nextVersion = existing.isEmpty()
                ? 1
                : existing.get(0).getVersion() + 1;

        ProjectBaseline saved = baselineRepository.save(
                new ProjectBaseline(
                        project,
                        nextVersion,
                        project.getBudget(),
                        project.getPlannedEndDate()
                )
        );

        return ProjectBaselineResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectBaselineResponse> findByProject(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }

        return baselineRepository
                .findByProjectIdOrderByVersionDesc(projectId)
                .stream()
                .map(ProjectBaselineResponse::from)
                .toList();
    }
}
