package com.construction.change.dto.response;

import com.construction.change.entity.ChangeRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ChangeRequestResponse(Long id, Long projectId, String projectCode, String title,
        String description, String category, BigDecimal estimatedCost, Integer estimatedDelayDays,
        String status, String decisionComment, LocalDateTime createdAt, LocalDateTime decidedAt) {
    public static ChangeRequestResponse from(ChangeRequest c) {
        return new ChangeRequestResponse(c.getId(), c.getProject().getId(), c.getProject().getProjectCode(),
            c.getTitle(), c.getDescription(), c.getCategory(), c.getEstimatedCost(), c.getEstimatedDelayDays(),
            c.getStatus(), c.getDecisionComment(), c.getCreatedAt(), c.getDecidedAt());
    }
}
