package com.construction.change.dto.request;

import jakarta.validation.constraints.Size;

public record DecisionRequest(@Size(max = 1000) String comment) {}
