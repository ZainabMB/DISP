package com.disp.automation.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "hire_order", schema = "probuilds")
public class HireOrder {

    @Id
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "tool_id")
    private Long toolId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total_price")
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "distribution_type", nullable = false)
    private DistributionType distributionType;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private HireStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_at_issue")
    private ToolCondition conditionAtIssue;

    @Column(name = "condition_notes")
    private String conditionNotes;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expected_return")
    private LocalDate expectedReturn;

    @Column(name = "actual_return")
    private LocalDate actualReturn;

    public enum PaymentMethod {
        CASH, CARD
    }

    public enum DistributionType {
        PICKUP, DELIVERY
    }

    public enum HireStatus {
        PENDING, PICKED_UP, RETURNED
    }

    public enum ToolCondition {
        NEW, GOOD, FAIR, DAMAGED
    }

    // existing getters and setters

    public ToolCondition getConditionAtIssue() { return conditionAtIssue; }
    public void setConditionAtIssue(ToolCondition conditionAtIssue) { this.conditionAtIssue = conditionAtIssue; }

    public String getConditionNotes() { return conditionNotes; }
    public void setConditionNotes(String conditionNotes) { this.conditionNotes = conditionNotes; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpectedReturn() { return expectedReturn; }
    public void setExpectedReturn(LocalDate expectedReturn) { this.expectedReturn = expectedReturn; }

    public LocalDate getActualReturn() { return actualReturn; }
    public void setActualReturn(LocalDate actualReturn) { this.actualReturn = actualReturn; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getToolId() { return toolId; }
    public void setToolId(Long toolId) { this.toolId = toolId; }

    public HireStatus getStatus() { return status; }
    public void setStatus(HireStatus status) { this.status = status; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public DistributionType getDistributionType() { return distributionType; }
    public void setDistributionType(DistributionType distributionType) { this.distributionType = distributionType; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
}