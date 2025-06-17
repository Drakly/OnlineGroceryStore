package com.online.shop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "routes")
public class RouteStep {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull(message = "Step index is required")
    @Min(value = 0, message = "Step index cannot be negative")
    @Column(name = "step_index", nullable = false)
    private Integer stepIndex;

    @NotNull(message = "X coordinate is required")
    @Column(nullable = false)
    private Integer x;

    @NotNull(message = "Y coordinate is required")
    @Column(nullable = false)
    private Integer y;

    public RouteStep() {}

    public RouteStep(Order order, Integer stepIndex, Integer x, Integer y) {
        this.order = order;
        this.stepIndex = stepIndex;
        this.x = x;
        this.y = y;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(Integer stepIndex) {
        this.stepIndex = stepIndex;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }
} 