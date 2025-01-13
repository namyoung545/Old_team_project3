package com.example.demo.entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fire_locations")
public class FireLocationData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="location_id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String locationMainCategory;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String locationSubCategory;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String locationDetail;

    // @ManyToOne
    // @JoinColumn(name = "fire_id", nullable = false, referencedColumnName = "id")
    // private FiresData fire;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
}
