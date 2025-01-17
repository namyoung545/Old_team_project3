package com.example.demo.service;

import com.example.demo.entity.dy_risk_entity;
import com.example.demo.repository.dy_risk_repository;
import com.example.demo.service.dy_risk_service;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class dy_risk_serviceImpl implements dy_risk_service {

    private final dy_risk_repository riskRepository;

    public dy_risk_serviceImpl(dy_risk_repository riskRepository) {
        this.riskRepository = riskRepository;
    }

    @Override
    public List<dy_risk_entity> getAllRisks() {
        return riskRepository.findAll();
    }

    @Override
    public dy_risk_entity getRiskById(Long id) {
        return riskRepository.findById(id).orElse(null);
    }

    @Override
    public dy_risk_entity saveRisk(dy_risk_entity risk) {
        return riskRepository.save(risk);
    }

    @Override
    public void deleteRisk(Long id) {
        riskRepository.deleteById(id);
    }
}
