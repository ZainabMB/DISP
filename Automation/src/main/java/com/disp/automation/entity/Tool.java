package com.disp.automation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "tool", schema = "probuilds")
public class Tool {

    public enum AvailabilityStatus {
        available, unavailable
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tool_id")
    private Long toolId;
    @Column(name = "tool_name", length = Integer.MAX_VALUE)
    private String toolName;

    @Column(name = "price")
    private Double price;

    @Column(name = "tool_type", length = Integer.MAX_VALUE)
    private String toolType;


    @Enumerated(EnumType.STRING)
    @Column(name = "availability", columnDefinition = "probuilds.availability_status", insertable = false, updatable = false)
    private AvailabilityStatus availability;

    public Long getToolId() {
        return toolId;
    }

    public void setToolId(Long toolId) {
        this.toolId = toolId;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getToolType() {
        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }


}