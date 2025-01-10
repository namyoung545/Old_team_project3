package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "ed_statistics", uniqueConstraints = { @UniqueConstraint(columnNames = { "year" }) })
public class EDStatisticsData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private Integer year;

    @Lob
    private String province;
    @Lob
    private String district;
    @Lob
    private String fire_type;
    @Lob
    private String heat_source_main;
    @Lob
    private String heat_source_sub;
    @Lob
    private String cause_main;
    @Lob
    private String cause_sub;
    @Lob
    private String ignition_material_main;
    @Lob
    private String ignition_material_sub;
    @Lob
    private String casualties_total;
    @Lob
    private String deaths;
    @Lob
    private String injuries;
    @Lob
    private String total_property_damage;
    @Lob
    private String location_main;
    @Lob
    private String location_mid;
    @Lob
    private String location_sub;
    
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
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }
    public String getFire_type() {
        return fire_type;
    }
    public void setFire_type(String fire_type) {
        this.fire_type = fire_type;
    }
    public String getHeat_source_main() {
        return heat_source_main;
    }
    public void setHeat_source_main(String heat_source_main) {
        this.heat_source_main = heat_source_main;
    }
    public String getHeat_source_sub() {
        return heat_source_sub;
    }
    public void setHeat_source_sub(String heat_source_sub) {
        this.heat_source_sub = heat_source_sub;
    }
    public String getCause_main() {
        return cause_main;
    }
    public void setCause_main(String cause_main) {
        this.cause_main = cause_main;
    }
    public String getCause_sub() {
        return cause_sub;
    }
    public void setCause_sub(String cause_sub) {
        this.cause_sub = cause_sub;
    }
    public String getIgnition_material_main() {
        return ignition_material_main;
    }
    public void setIgnition_material_main(String ignition_material_main) {
        this.ignition_material_main = ignition_material_main;
    }
    public String getIgnition_material_sub() {
        return ignition_material_sub;
    }
    public void setIgnition_material_sub(String ignition_material_sub) {
        this.ignition_material_sub = ignition_material_sub;
    }
    public String getCasualties_total() {
        return casualties_total;
    }
    public void setCasualties_total(String casualties_total) {
        this.casualties_total = casualties_total;
    }
    public String getDeaths() {
        return deaths;
    }
    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }
    public String getInjuries() {
        return injuries;
    }
    public void setInjuries(String injuries) {
        this.injuries = injuries;
    }
    public String getTotal_property_damage() {
        return total_property_damage;
    }
    public void setTotal_property_damage(String total_property_damage) {
        this.total_property_damage = total_property_damage;
    }
    public String getLocation_main() {
        return location_main;
    }
    public void setLocation_main(String location_main) {
        this.location_main = location_main;
    }
    public String getLocation_mid() {
        return location_mid;
    }
    public void setLocation_mid(String location_mid) {
        this.location_mid = location_mid;
    }
    public String getLocation_sub() {
        return location_sub;
    }
    public void setLocation_sub(String location_sub) {
        this.location_sub = location_sub;
    }
}
