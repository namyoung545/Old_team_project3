package com.example.demo.service;

import com.example.demo.entity.dy_risk_entity;
import java.util.List;

public interface dy_risk_service {
    List<dy_risk_entity> getAllRisks();
    dy_risk_entity getRiskById(Long id);
    dy_risk_entity saveRisk(dy_risk_entity risk);
    void deleteRisk(Long id);
}
