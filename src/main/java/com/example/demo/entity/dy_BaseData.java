package com.example.demo.entity;
 
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class dy_statisticData {
    private String casualtiesTotal; // 전체 사상자 수
    private String totalPropertyDamage; // 총 재산피해액
    private String locationMain; // 발화지역(대)

     // Getters and Setters
     public String getCasualtiesTotal() {
        return casualtiesTotal;
    }

    public void setCasualtiesTotal(String casualtiesTotal) {
        this.casualtiesTotal = casualtiesTotal;
    }

    public String getTotalPropertyDamage() {
        return totalPropertyDamage;
    }

    public void setTotalPropertyDamage(String totalPropertyDamage) {
        this.totalPropertyDamage = totalPropertyDamage;
    }

    public String getLocationMain() {
        return locationMain;
    }

    public void setLocationMain(String locationMain) {
        this.locationMain = locationMain;
    }
}