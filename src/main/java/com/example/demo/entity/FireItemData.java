package com.example.demo.entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fire_items")
public class FireItemData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="item_id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String itemCategory;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String itemDetail;

    // @ManyToOne
    // @JoinColumn(name = "fire_id", nullable = false, referencedColumnName = "id")
    // private FiresData fire;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
}
