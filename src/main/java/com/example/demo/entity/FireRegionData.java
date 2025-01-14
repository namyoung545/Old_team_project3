package com.example.demo.entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fire_regions")
public class FireRegionData {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "region_id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String regionProvince;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String regionCity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegionProvince() {
        return regionProvince;
    }

    public void setRegionProvince(String regionProvince) {
        this.regionProvince = regionProvince;
    }

    public String getRegionCity() {
        return regionCity;
    }

    public void setRegionCity(String regionCity) {
        this.regionCity = regionCity;
    }
    
    
}
