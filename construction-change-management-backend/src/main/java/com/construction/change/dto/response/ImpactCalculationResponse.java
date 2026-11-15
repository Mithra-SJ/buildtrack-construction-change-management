
package com.construction.change.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ImpactCalculationResponse(
        Long projectId,
        Forecast approvedForecast,
        PendingExposure pendingExposure,
        Forecast scenarioForecast
) {
    public record Forecast(
            BigDecimal budget,
            LocalDate completionDate
    ) {}

    public record PendingExposure(
            BigDecimal cost,
            Integer delayDays
    ) {}
}
