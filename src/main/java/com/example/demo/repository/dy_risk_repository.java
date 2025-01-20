package com.example.demo.repository;

import com.example.demo.entity.dy_risk_entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface dy_risk_repository extends JpaRepository<dy_risk_entity, Long> {
    // Custom queries can be added here if needed
}
