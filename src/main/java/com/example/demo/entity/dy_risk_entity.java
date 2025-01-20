package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dy_risk")
public class dy_risk_entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false, length = 255)
    private String region;

    @Column(nullable = false)
    private int totalIncidents;

    @Column(nullable = false)
    private long totalDamage;

    @Column(nullable = true)
    private Integer riskLevel;

    @Column(nullable = true)
    private Float riskScore;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public int getTotalIncidents() { return totalIncidents; }
    public void setTotalIncidents(int totalIncidents) { this.totalIncidents = totalIncidents; }

    public long getTotalDamage() { return totalDamage; }
    public void setTotalDamage(long totalDamage) { this.totalDamage = totalDamage; }

    public Integer getRiskLevel() { return riskLevel; }
    public void setRiskLevel(Integer riskLevel) { this.riskLevel = riskLevel; }

    public Float getRiskScore() { return riskScore; }
    public void setRiskScore(Float riskScore) { this.riskScore = riskScore; }
}
