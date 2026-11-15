
package com.construction.change.controller;

import com.construction.change.dto.response.ImpactCalculationResponse;
import com.construction.change.service.ImpactService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/impact")
public class ImpactController {

    private final ImpactService impactService;

    public ImpactController(ImpactService impactService) {
        this.impactService = impactService;
    }

    @GetMapping
    public ImpactCalculationResponse calculateImpact(
            @PathVariable Long projectId) {
        return impactService.calculateImpact(projectId);
    }
}
