package com.example.demo.entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fire_causes")
public class FireCauseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="cause_id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String causeCategory;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String causeSubcategory;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCauseCategory() {
        return causeCategory;
    }

    public void setCauseCategory(String causeCategory) {
        this.causeCategory = causeCategory;
    }

    public String getCauseSubcategory() {
        return causeSubcategory;
    }

    public void setCauseSubcategory(String causeSubcategory) {
        this.causeSubcategory = causeSubcategory;
    }

    @Override
    public String toString() {
        return "FireCauseData [id=" + id + ", causeCategory=" + causeCategory + ", causeSubcategory=" + causeSubcategory
                + "]";
    }
}
