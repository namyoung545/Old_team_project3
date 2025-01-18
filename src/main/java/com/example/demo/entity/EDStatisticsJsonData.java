package com.example.demo.entity;

import java.util.Map;

public class EDStatisticsJsonData {

    private Integer year;
    private Map<String, Integer> hourly_analysis;
    private Map<String, Integer> province;
    private Map<String, Integer> district;
    private Map<String, Integer> fire_type;
    private Map<String, Integer> heat_source_main;
    private Map<String, Integer> heat_source_sub;
    private Map<String, Integer> cause_main;
    private Map<String, Integer> cause_sub;
    private Map<String, Integer> ignition_material_main;
    private Map<String, Integer> ignition_material_sub;
    private Map<String, Integer> casualties_total;
    private Map<String, Integer> deaths;
    private Map<String, Integer> injuries;
    private Integer total_property_damage;
    private Map<String, Integer> location_counts;
    private Map<String, Integer> location_mid;
    private Map<String, Integer> location_sub;

    // Getter and Setter methods

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Map<String, Integer> getHourly_analysis() {
        return hourly_analysis;
    }

    public void setHourly_analysis(Map<String, Integer> hourly_analysis) {
        this.hourly_analysis = hourly_analysis;
    }

    public Map<String, Integer> getProvince() {
        return province;
    }

    public void setProvince(Map<String, Integer> province) {
        this.province = province;
    }

    public Map<String, Integer> getDistrict() {
        return district;
    }

    public void setDistrict(Map<String, Integer> district) {
        this.district = district;
    }

    public Map<String, Integer> getFire_type() {
        return fire_type;
    }

    public void setFire_type(Map<String, Integer> fire_type) {
        this.fire_type = fire_type;
    }

    public Map<String, Integer> getHeat_source_main() {
        return heat_source_main;
    }

    public void setHeat_source_main(Map<String, Integer> heat_source_main) {
        this.heat_source_main = heat_source_main;
    }

    public Map<String, Integer> getHeat_source_sub() {
        return heat_source_sub;
    }

    public void setHeat_source_sub(Map<String, Integer> heat_source_sub) {
        this.heat_source_sub = heat_source_sub;
    }

    public Map<String, Integer> getCause_main() {
        return cause_main;
    }

    public void setCause_main(Map<String, Integer> cause_main) {
        this.cause_main = cause_main;
    }

    public Map<String, Integer> getCause_sub() {
        return cause_sub;
    }

    public void setCause_sub(Map<String, Integer> cause_sub) {
        this.cause_sub = cause_sub;
    }

    public Map<String, Integer> getIgnition_material_main() {
        return ignition_material_main;
    }

    public void setIgnition_material_main(Map<String, Integer> ignition_material_main) {
        this.ignition_material_main = ignition_material_main;
    }

    public Map<String, Integer> getIgnition_material_sub() {
        return ignition_material_sub;
    }

    public void setIgnition_material_sub(Map<String, Integer> ignition_material_sub) {
        this.ignition_material_sub = ignition_material_sub;
    }

    public Map<String, Integer> getCasualties_total() {
        return casualties_total;
    }

    public void setCasualties_total(Map<String, Integer> casualties_total) {
        this.casualties_total = casualties_total;
    }

    public Map<String, Integer> getDeaths() {
        return deaths;
    }

    public void setDeaths(Map<String, Integer> deaths) {
        this.deaths = deaths;
    }

    public Map<String, Integer> getInjuries() {
        return injuries;
    }

    public void setInjuries(Map<String, Integer> injuries) {
        this.injuries = injuries;
    }

    public Integer getTotal_property_damage() {
        return total_property_damage;
    }

    public void setTotal_property_damage(Integer total_property_damage) {
        this.total_property_damage = total_property_damage;
    }

    public Map<String, Integer> getLocation_counts() {
        return location_counts;
    }

    public void setLocation_counts(Map<String, Integer> location_counts) {
        this.location_counts = location_counts;
    }

    public Map<String, Integer> getLocation_mid() {
        return location_mid;
    }

    public void setLocation_mid(Map<String, Integer> location_mid) {
        this.location_mid = location_mid;
    }

    public Map<String, Integer> getLocation_sub() {
        return location_sub;
    }

    public void setLocation_sub(Map<String, Integer> location_sub) {
        this.location_sub = location_sub;
    }
}
