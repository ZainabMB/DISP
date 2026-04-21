package com.disp.automation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tool", schema = "probuilds")
public class Tool {

    public enum AvailabilityStatus {
        available, unavailable
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tool_id_gen")
    @SequenceGenerator(name = "tool_id_gen", sequenceName = "tool_tool_id_seq", allocationSize = 1)
    @Column(name = "tool_id", nullable = false)
    private Long toolId;

    @Column(name = "tool_name", length = Integer.MAX_VALUE)
    private String toolName;

    @Column(name = "price")
    private Double price;

    @Column(name = "tool_type", length = Integer.MAX_VALUE)
    private String toolType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", columnDefinition = "probuilds.availability_status", insertable = false, updatable = false)
    private AvailabilityStatus availability;

    public Long getId() {
        return toolId;
    }

    public void setId(Long toolId) {
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public AvailabilityStatus getAvailability() {
        return availability;
    }

}