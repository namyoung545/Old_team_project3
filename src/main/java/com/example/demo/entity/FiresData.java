package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "fires")
public class FiresData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String regionProvince;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String regionCity;

    @Column(nullable = false)
    @ColumnDefault("'미확인'")
    private String fireType;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer damageProperty;

    @Column(name = "casualties_deaths", nullable = false)
    @ColumnDefault("0")
    private Integer deaths;

    @Column(name = "casualties_injuries", nullable = false)
    @ColumnDefault("0")
    private Integer injuries;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fire_cause_id", nullable = false)
    private FireCauseData fireCause;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fire_location_id", nullable = false)
    private FireLocationData fireLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fire_item_id", nullable = false)
    private FireItemData fireItem;

    // @OneToMany(mappedBy = "fire", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<FireCauseData> fireCauses = new ArrayList<>();

    // @OneToMany(mappedBy = "fire", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<FireLocationData> fireLocations = new ArrayList<>();

    // @OneToMany(mappedBy = "fire", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<FireItemData> fireItems = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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

    public String getFireType() {
        return fireType;
    }

    public void setFireType(String fireType) {
        this.fireType = fireType;
    }

    public Integer getDamageProperty() {
        return damageProperty;
    }

    public void setDamageProperty(Integer damageProperty) {
        this.damageProperty = damageProperty;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }

    public Integer getInjuries() {
        return injuries;
    }

    public void setInjuries(Integer injuries) {
        this.injuries = injuries;
    }

    public FireCauseData getFireCause() {
        return fireCause;
    }

    public void setFireCause(FireCauseData fireCause) {
        this.fireCause = fireCause;
    }

    public FireLocationData getFireLocation() {
        return fireLocation;
    }

    public void setFireLocation(FireLocationData fireLocation) {
        this.fireLocation = fireLocation;
    }

    public FireItemData getFireItem() {
        return fireItem;
    }

    public void setFireItem(FireItemData fireItem) {
        this.fireItem = fireItem;
    }

}
