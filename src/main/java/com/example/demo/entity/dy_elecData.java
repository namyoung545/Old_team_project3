package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dy_merged_fire_data")

public class dy_elecData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer year;

    private String region;

    @Column(name = "total_incidents")
    private Integer totalIncidents;

    @Column(name = "total_damage")
    private Long totalDamage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getTotalIncidents() {
        return totalIncidents;
    }

    public void setTotalIncidents(Integer totalIncidents) {
        this.totalIncidents = totalIncidents;
    }

    public Long getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(Long totalDamage) {
        this.totalDamage = totalDamage;
    }
    
}
