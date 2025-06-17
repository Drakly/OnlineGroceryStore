package com.online.shop.dto;

import com.online.shop.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponseDto {

    private UUID orderId;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;
    private List<List<Integer>> visitedLocations;
    private String message;
    private List<String> missingItems;

    public OrderResponseDto() {}

    public OrderResponseDto(UUID orderId, Order.OrderStatus status, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public Order.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<List<Integer>> getVisitedLocations() {
        return visitedLocations;
    }

    public void setVisitedLocations(List<List<Integer>> visitedLocations) {
        this.visitedLocations = visitedLocations;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getMissingItems() {
        return missingItems;
    }

    public void setMissingItems(List<String> missingItems) {
        this.missingItems = missingItems;
    }
} 