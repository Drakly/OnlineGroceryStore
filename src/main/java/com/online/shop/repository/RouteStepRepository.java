package com.online.shop.repository;

import com.online.shop.entity.RouteStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RouteStepRepository extends JpaRepository<RouteStep, UUID> {
    
    @Query("SELECT r FROM RouteStep r WHERE r.order.id = :orderId ORDER BY r.stepIndex ASC")
    List<RouteStep> findByOrderIdOrderByStepIndexAsc(@Param("orderId") UUID orderId);
} 