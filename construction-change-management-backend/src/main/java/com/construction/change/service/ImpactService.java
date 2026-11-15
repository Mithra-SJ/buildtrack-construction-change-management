package com.construction.change.service;

import com.construction.change.dto.response.ImpactCalculationResponse;

public interface ImpactService {
    ImpactCalculationResponse calculateImpact(Long projectId);
}
