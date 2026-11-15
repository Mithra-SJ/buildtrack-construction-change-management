package com.construction.change.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_baselines", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "version"}))
public class ProjectBaseline {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private Project project;
    @Column(nullable = false)
    private Integer version;
    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal approvedBudget;
    @Column(nullable = false)
    private LocalDate plannedEndDate;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected ProjectBaseline() {}
    public ProjectBaseline(Project project, Integer version, BigDecimal approvedBudget, LocalDate plannedEndDate) {
        this.project = project; this.version = version; this.approvedBudget = approvedBudget; this.plannedEndDate = plannedEndDate;
    }
    public Long getId() { return id; }
    public Project getProject() { return project; }
    public Integer getVersion() { return version; }
    public BigDecimal getApprovedBudget() { return approvedBudget; }
    public LocalDate getPlannedEndDate() { return plannedEndDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
