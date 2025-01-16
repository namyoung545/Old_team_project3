package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dy_merged_fire_data") // DB 테이블 이름
public class dy_elecData {

    @Id
    private Long id; // Primary Key에 해당하는 필드

    private String region;
    private Integer totalIncidents;
    private Integer totalDamage;
    private Integer year;

    // Getter와 Setter 추가
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(Integer totalDamage) {
        this.totalDamage = totalDamage;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
