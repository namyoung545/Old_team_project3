package com.example.demo.entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fire_ignitions")
public class FireIgnitionData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "ignition_id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String ignitionSourceCategory;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String ignitionSourceSubcategory;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIgnitionSourceCategory() {
        return ignitionSourceCategory;
    }

    public void setIgnitionSourceCategory(String ignitionSourceCategory) {
        this.ignitionSourceCategory = ignitionSourceCategory;
    }

    public String getIgnitionSourceSubcategory() {
        return ignitionSourceSubcategory;
    }

    public void setIgnitionSourceSubcategory(String ignitionSourceSubcategory) {
        this.ignitionSourceSubcategory = ignitionSourceSubcategory;
    }
    
    
}
