package com.construction.change.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateProjectRequest(
    @NotBlank @Size(max = 30) String projectCode,
    @NotBlank @Size(max = 150) String name,
    @NotNull @DecimalMin("0.01") BigDecimal budget,
    @NotNull LocalDate startDate,
    @NotNull LocalDate plannedEndDate
) {}
