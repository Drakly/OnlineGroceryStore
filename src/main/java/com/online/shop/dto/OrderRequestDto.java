package com.online.shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class OrderRequestDto {

    @NotNull(message = "Order items are required")
    private Map<@NotBlank String, @NotNull @Min(1) Integer> items;

    // Constructors
    public OrderRequestDto() {}

    public OrderRequestDto(Map<String, Integer> items) {
        this.items = items;
    }

    // Getters and Setters
    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }
} 