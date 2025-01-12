package com.example.demo.entity;

import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "fire_itmes")
public class FireItemData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("미확인")
    private String itemCategory;

    @Column(nullable = false)
    @ColumnDefault("미확인")
    private String itemDetail;

    @ManyToOne
    @JoinColumn(name = "fire_id", nullable = false)
    private FiresData fire;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(String itemDetail) {
        this.itemDetail = itemDetail;
    }

    public FiresData getFire() {
        return fire;
    }

    public void setFire(FiresData fire) {
        this.fire = fire;
    }
}
