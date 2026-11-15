
package com.construction.change.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ImpactCalculationRequest(
        Long projectId,
        BaselineInput baseline,
        List<ChangeRequestInput> changeRequests
) {
    public record BaselineInput(
            BigDecimal approvedBudget,
            LocalDate plannedEndDate
    ) {}

    public record ChangeRequestInput(
            Long id,
            BigDecimal estimatedCost,
            Integer estimatedDelayDays,
            String status
    ) {}
}
