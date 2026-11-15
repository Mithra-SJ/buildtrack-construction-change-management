package com.construction.change.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateChangeRequest(
    @NotNull @Positive Long projectId,
    @NotBlank @Size(max = 160) String title,
    @NotBlank @Size(max = 2000) String description,
    @NotBlank @Size(max = 40) String category,
    @NotNull @DecimalMin("0.00") BigDecimal estimatedCost,
    @NotNull @Min(0) Integer estimatedDelayDays
) {}
