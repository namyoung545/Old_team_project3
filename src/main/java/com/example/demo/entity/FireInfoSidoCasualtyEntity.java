package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fire_info_sido_casualty")
public class FireInfoSidoCasualtyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    // 시도명
    @Column(nullable = false)
    private String sidoNm;

    // 사고자수
    @Column(nullable = false)
    private Integer vctmPercnt;

    // 부상자수
    @Column(nullable = false)
    private Integer injrdprPercnt;

    // 인명피해수
    @Column(nullable = false)
    private Integer lifeDmgPercnt;

    // 화재발생건수
    @Column(nullable =false)
    private Integer ocrnMnb;

    // 발생일자
    @Column(nullable = false)
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

    public Integer getVctmPercnt() {
        return vctmPercnt;
    }

    public void setVctmPercnt(Integer vctmPercnt) {
        this.vctmPercnt = vctmPercnt;
    }

    public Integer getInjrdprPercnt() {
        return injrdprPercnt;
    }

    public void setInjrdprPercnt(Integer injrdprPercnt) {
        this.injrdprPercnt = injrdprPercnt;
    }

    public Integer getLifeDmgPercnt() {
        return lifeDmgPercnt;
    }

    public void setLifeDmgPercnt(Integer lifeDmgPercnt) {
        this.lifeDmgPercnt = lifeDmgPercnt;
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