package com.disp.automation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_tool_instance", schema = "probuilds")
public class OrderToolInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "instance_id", nullable = false)
    private Long instanceId;

    @Column(name = "order_type", nullable = false)
    private String orderType; // "HIRE" or "SALE"

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Long getInstanceId() { return instanceId; }
    public void setInstanceId(Long instanceId) { this.instanceId = instanceId; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
}