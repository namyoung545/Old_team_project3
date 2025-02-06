package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.example.demo.repository.aiRiskRepository;

public class aiRiskService {
        private final aiRiskRepository riskRepository;

    public aiRiskService(aiRiskRepository riskRepository) {
        this.riskRepository = riskRepository;
    }

    public List<Map<String, Object>> getRiskData() {
        return riskRepository.getRiskData();
    }
}
