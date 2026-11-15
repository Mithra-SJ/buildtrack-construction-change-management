package com.construction.change.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects", uniqueConstraints = @UniqueConstraint(columnNames = "project_code"))
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "project_code", nullable = false, unique = true, length = 30)
    private String projectCode;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal budget;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate plannedEndDate;
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Project() {}
    public Project(String projectCode, String name, BigDecimal budget, LocalDate startDate, LocalDate plannedEndDate) {
        this.projectCode = projectCode; this.name = name; this.budget = budget;
        this.startDate = startDate; this.plannedEndDate = plannedEndDate;
    }
    public Long getId() { return id; }
    public String getProjectCode() { return projectCode; }
    public String getName() { return name; }
    public BigDecimal getBudget() { return budget; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getPlannedEndDate() { return plannedEndDate; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setName(String name) { this.name = name; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setPlannedEndDate(LocalDate plannedEndDate) { this.plannedEndDate = plannedEndDate; }
    public void setStatus(String status) { this.status = status; }
}
