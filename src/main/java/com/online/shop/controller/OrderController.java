package com.online.shop.controller;

import com.online.shop.dto.OrderRequestDto;
import com.online.shop.dto.OrderResponseDto;
import com.online.shop.entity.User;
import com.online.shop.service.OrderService;
import com.online.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequest,
                                                       Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OrderResponseDto response = orderService.processOrder(user, orderRequest);
        
        if (response.getStatus() == com.online.shop.entity.Order.OrderStatus.SUCCESS) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderStatus(@PathVariable UUID id) {
        OrderResponseDto response = orderService.getOrderStatus(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/routes")
    public ResponseEntity<OrderResponseDto> getOrderRoute(@RequestParam UUID orderId) {
        OrderResponseDto response = orderService.getOrderStatus(orderId);
        return ResponseEntity.ok(response);
    }
} 