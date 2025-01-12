package com.example.demo.entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "fire_locations")
public class FireLocationData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("미확인")
    private String locationMainCategory;

    @Column(nullable = false)
    @ColumnDefault("미확인")
    private String locationSubCategory;

    @Column(nullable = false)
    @ColumnDefault("미확인")
    private String locationDetail;

    @ManyToOne
    @JoinColumn(name = "fire_id", nullable = false)
    private FiresData fire;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocationMainCategory() {
        return locationMainCategory;
    }

    public void setLocationMainCategory(String locationMainCategory) {
        this.locationMainCategory = locationMainCategory;
    }

    public String getLocationSubCategory() {
        return locationSubCategory;
    }

    public void setLocationSubCategory(String locationSubCategory) {
        this.locationSubCategory = locationSubCategory;
    }

    public String getLocationDetail() {
        return locationDetail;
    }

    public void setLocationDetail(String locationDetail) {
        this.locationDetail = locationDetail;
    }

    public FiresData getFire() {
        return fire;
    }

    public void setFire(FiresData fire) {
        this.fire = fire;
    }

}
