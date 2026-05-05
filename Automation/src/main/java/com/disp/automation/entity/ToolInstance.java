package com.disp.automation.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tool_instance", schema = "probuilds")
public class ToolInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instance_id")
    private Long instanceId;

    @Column(name = "tool_id", nullable = false)
    private Long toolId;

    @Column(name = "serial_number", unique = true)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ToolInstanceStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "notes")
    private String notes;

    public enum ToolInstanceStatus {
        AVAILABLE, HIRED, MAINTENANCE, RETIRED, SOLD
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDate.now();
        if (status == null) status = ToolInstanceStatus.AVAILABLE;
    }

    // getters and setters
    public Long getInstanceId() { return instanceId; }
    public void setInstanceId(Long instanceId) { this.instanceId = instanceId; }

    public Long getToolId() { return toolId; }
    public void setToolId(Long toolId) { this.toolId = toolId; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public ToolInstanceStatus getStatus() { return status; }
    public void setStatus(ToolInstanceStatus status) { this.status = status; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}