package com.example.demo.service;

import com.example.demo.repository.dy_aiRiskRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class dy_aiRiskService {
    private final dy_aiRiskRepository riskRepository;

    public dy_aiRiskService(dy_aiRiskRepository riskRepository) {
        this.riskRepository = riskRepository;
    }

    public List<Map<String, Object>> getRiskData() {
        return riskRepository.getRiskData();
    }
}
