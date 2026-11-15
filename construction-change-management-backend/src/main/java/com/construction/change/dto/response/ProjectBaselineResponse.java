
package com.construction.change.dto.response;

import com.construction.change.entity.ProjectBaseline;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectBaselineResponse(
        Long id,
        Long projectId,
        String projectCode,
        String projectName,
        Integer version,
        BigDecimal approvedBudget,
        LocalDate plannedEndDate,
        LocalDateTime createdAt
) {
    public static ProjectBaselineResponse from(ProjectBaseline baseline) {
        return new ProjectBaselineResponse(
                baseline.getId(),
                baseline.getProject().getId(),
                baseline.getProject().getProjectCode(),
                baseline.getProject().getName(),
                baseline.getVersion(),
                baseline.getApprovedBudget(),
                baseline.getPlannedEndDate(),
                baseline.getCreatedAt()
        );
    }
}
