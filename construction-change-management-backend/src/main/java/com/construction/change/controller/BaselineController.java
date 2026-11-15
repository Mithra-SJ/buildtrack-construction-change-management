
package com.construction.change.controller;

import com.construction.change.dto.response.ProjectBaselineResponse;
import com.construction.change.service.BaselineService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/baselines")
public class BaselineController {

    private final BaselineService baselineService;

    public BaselineController(BaselineService baselineService) {
        this.baselineService = baselineService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectBaselineResponse createInitial(
            @PathVariable Long projectId) {
        return baselineService.createInitial(projectId);
    }

    @GetMapping
    public List<ProjectBaselineResponse> findByProject(
            @PathVariable Long projectId) {
        return baselineService.findByProject(projectId);
    }
}
