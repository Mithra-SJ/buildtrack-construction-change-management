package com.construction.change.service.impl;

import com.construction.change.dto.request.CreateChangeRequest;
import com.construction.change.dto.response.ChangeRequestResponse;
import com.construction.change.entity.ChangeRequest;
import com.construction.change.entity.Project;
import com.construction.change.exception.BadRequestException;
import com.construction.change.exception.ResourceNotFoundException;
import com.construction.change.repository.ChangeRequestRepository;
import com.construction.change.repository.ProjectRepository;
import com.construction.change.service.ChangeRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ChangeRequestServiceImpl implements ChangeRequestService {
    private final ProjectRepository projectRepository;
    private final ChangeRequestRepository changeRequestRepository;
    public ChangeRequestServiceImpl(ProjectRepository projectRepository, ChangeRequestRepository changeRequestRepository) {
        this.projectRepository = projectRepository; this.changeRequestRepository = changeRequestRepository;
    }
    @Override
    public ChangeRequestResponse create(CreateChangeRequest request) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));
        ChangeRequest change = new ChangeRequest(project, request.title(), request.description(), request.category(),
                request.estimatedCost(), request.estimatedDelayDays());
        return ChangeRequestResponse.from(changeRequestRepository.save(change));
    }
    @Override @Transactional(readOnly = true)
    public List<ChangeRequestResponse> findAll(Long projectId) {
        List<ChangeRequest> records = projectId == null ? changeRequestRepository.findAll()
                : changeRequestRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        return records.stream().map(ChangeRequestResponse::from).toList();
    }
    @Override
    public ChangeRequestResponse decide(Long id, boolean approve, String comment) {
        ChangeRequest change = changeRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Change request not found: " + id));
        if (!"DRAFT".equals(change.getStatus()) && !"SUBMITTED".equals(change.getStatus()))
            throw new BadRequestException("Only DRAFT or SUBMITTED requests can be decided");
        change.decide(approve ? "APPROVED" : "REJECTED", comment);
        return ChangeRequestResponse.from(changeRequestRepository.save(change));
    }
}
