package com.construction.change.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "change_requests")
public class ChangeRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private Project project;
    @Column(nullable = false, length = 160)
    private String title;
    @Column(nullable = false, length = 2000)
    private String description;
    @Column(nullable = false, length = 40)
    private String category;
    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal estimatedCost;
    @Column(nullable = false)
    private Integer estimatedDelayDays;
    @Column(nullable = false, length = 20)
    private String status = "DRAFT";
    @Column(length = 1000)
    private String decisionComment;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime decidedAt;

    protected ChangeRequest() {}
    public ChangeRequest(Project project, String title, String description, String category, BigDecimal estimatedCost, Integer estimatedDelayDays) {
        this.project = project; this.title = title; this.description = description; this.category = category;
        this.estimatedCost = estimatedCost; this.estimatedDelayDays = estimatedDelayDays;
    }
    public Long getId() { return id; }
    public Project getProject() { return project; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public Integer getEstimatedDelayDays() { return estimatedDelayDays; }
    public String getStatus() { return status; }
    public String getDecisionComment() { return decisionComment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setStatus(String status) { this.status = status; }
    public void decide(String status, String comment) { this.status = status; this.decisionComment = comment; this.decidedAt = LocalDateTime.now(); }
}
