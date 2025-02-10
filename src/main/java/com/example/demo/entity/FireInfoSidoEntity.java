package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fire_info_sido")
public class FireInfoSidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    // 시도명
    @Column(nullable = false)
    private String sido_nm;

    // 화재접수건수
    @Column(nullable = false)
    private Integer fire_rcpt_mnb = 0;

    // 상황종료건수
    @Column(nullable = false)
    private Integer stn_end_mnb = 0;

    // 자체진화건수
    @Column(nullable = false)
    private Integer slf_extsh_mnb = 0;

    // 오보처리건수
    @Column(nullable = false)
    private Integer flsrp_prcs_mnb = 0;

    // 허위신고건수
    @Column(nullable = false)
    private Integer fals_dclr_mnb = 0;

    // 발생일자
    @Column(nullable = false)
    private LocalDate ocrn_ymd;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSido_nm() {
        return sido_nm;
    }

    public void setSido_nm(String sido_nm) {
        this.sido_nm = sido_nm;
    }

    public Integer getFire_rcpt_mnb() {
        return fire_rcpt_mnb;
    }

    public void setFire_rcpt_mnb(Integer fire_rcpt_mnb) {
        this.fire_rcpt_mnb = fire_rcpt_mnb;
    }

    public Integer getStn_end_mnb() {
        return stn_end_mnb;
    }

    public void setStn_end_mnb(Integer stn_end_mnb) {
        this.stn_end_mnb = stn_end_mnb;
    }

    public Integer getSlf_extsh_mnb() {
        return slf_extsh_mnb;
    }

    public void setSlf_extsh_mnb(Integer slf_extsh_mnb) {
        this.slf_extsh_mnb = slf_extsh_mnb;
    }

    public Integer getFlsrp_prcs_mnb() {
        return flsrp_prcs_mnb;
    }

    public void setFlsrp_prcs_mnb(Integer flsrp_prcs_mnb) {
        this.flsrp_prcs_mnb = flsrp_prcs_mnb;
    }

    public Integer getFals_dclr_mnb() {
        return fals_dclr_mnb;
    }

    public void setFals_dclr_mnb(Integer fals_dclr_mnb) {
        this.fals_dclr_mnb = fals_dclr_mnb;
    }

    public LocalDate getOcrn_ymd() {
        return ocrn_ymd;
    }

    public void setOcrn_ymd(LocalDate ocrn_ymd) {
        this.ocrn_ymd = ocrn_ymd;
    }

    
}
