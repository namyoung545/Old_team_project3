package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fire_info_sido_damage")
public class FireInfoSidoDamageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    // 시도명
    @Column(nullable = false)
    private String sidoNm;

    // 재산피해
    @Column(nullable = false)
    private Integer prptDmgSbttAmt = 0;

    // 화재발생건수
    @Column(nullable =false)
    private Integer ocrnMnb = 0;

    // 발생일자
    @Column (nullable = false)
    private LocalDate ocrnYmd;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSidoNm() {
        return sidoNm;
    }

    public void setSidoNm(String sidoNm) {
        this.sidoNm = sidoNm;
    }

    public Integer getPrptDmgSbttAmt() {
        return prptDmgSbttAmt;
    }

    public void setPrptDmgSbttAmt(Integer prptDmgSbttAmt) {
        this.prptDmgSbttAmt = prptDmgSbttAmt;
    }

    public Integer getOcrnMnb() {
        return ocrnMnb;
    }

    public void setOcrnMnb(Integer ocrnMnb) {
        this.ocrnMnb = ocrnMnb;
    }

    public LocalDate getOcrnYmd() {
        return ocrnYmd;
    }

    public void setOcrnYmd(LocalDate ocrnYmd) {
        this.ocrnYmd = ocrnYmd;
    }
   
    
}
