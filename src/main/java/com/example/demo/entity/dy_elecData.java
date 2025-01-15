package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "dy_fires")
public class dy_elecData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    // @Column(name = "region")
    // private String region;

    // @Column(name = "total_incidents")
    // private Long totalIncidents;

    // @Column(name = "total_damage")
    // private Long totalDamage;

    // public dy_elecData() {}

    //  // JPQL 쿼리를 위한 생성자
    //  public dy_elecData(Integer year, String region, Long totalIncidents, Long totalDamage) {
    //     this.dateTime = LocalDateTime.of(year, 1, 1, 0, 0);
    //     this.region = region;
    //     this.totalIncidents = totalIncidents;
    //     this.totalDamage = totalDamage;
    // }
 
    //  // Getter와 Setter
    //  public LocalDateTime getDateTime() {
    //      return dateTime;
    //  }
 
    //  public void setDateTime(LocalDateTime dateTime) {
    //      this.dateTime = dateTime;
    //  }
 
    //  public String getRegion() {
    //      return region;
    //  }
 
    //  public void setRegion(String region) {
    //      this.region = region;
    //  }
 
    //  public Long getTotalIncidents() {
    //      return totalIncidents;
    //  }
 
    //  public void setTotalIncidents(Long totalIncidents) {
    //      this.totalIncidents = totalIncidents;
    //  }
 
    //  public Long getTotalDamage() {
    //      return totalDamage;
    //  }
 
    //  public void setTotalDamage(Long totalDamage) {
    //      this.totalDamage = totalDamage;
    //  }
 }