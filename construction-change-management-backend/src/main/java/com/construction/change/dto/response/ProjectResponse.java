package com.construction.change.dto.response;

import com.construction.change.entity.Project;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectResponse(Long id, String projectCode, String name, BigDecimal budget,
        LocalDate startDate, LocalDate plannedEndDate, String status) {
    public static ProjectResponse from(Project p) {
        return new ProjectResponse(p.getId(), p.getProjectCode(), p.getName(), p.getBudget(),
                p.getStartDate(), p.getPlannedEndDate(), p.getStatus());
    }
}
