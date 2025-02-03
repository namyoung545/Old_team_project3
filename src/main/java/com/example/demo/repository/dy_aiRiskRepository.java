package com.example.demo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class dy_aiRiskRepository {

    private final JdbcTemplate jdbcTemplate;

    public dy_aiRiskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getRiskData() {
        String sql = "SELECT Region, Cause, `Reduction (%)` FROM dy_risk_analysis ORDER BY `Reduction (%)` DESC";
        return jdbcTemplate.queryForList(sql);
    }
}
