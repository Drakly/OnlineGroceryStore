package com.online.shop.repository;

import com.online.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    Optional<Product> findByXAndY(Integer x, Integer y);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p WHERE p.quantity > 0")
    List<Product> findAllAvailableProducts();
    
    @Query("SELECT p FROM Product p WHERE p.name IN :names")
    List<Product> findByNameIn(@Param("names") List<String> names);
    
    boolean existsByXAndY(Integer x, Integer y);
} 