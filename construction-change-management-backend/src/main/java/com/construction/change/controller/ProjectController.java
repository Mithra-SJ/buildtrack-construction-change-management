package com.construction.change.controller;

import com.construction.change.dto.request.CreateProjectRequest;
import com.construction.change.dto.response.ProjectResponse;
import com.construction.change.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    public ProjectController(ProjectService projectService) { this.projectService = projectService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) { return projectService.create(request); }

    @GetMapping
    public List<ProjectResponse> findAll() { return projectService.findAll(); }

    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable Long id) { return projectService.findById(id); }
}
