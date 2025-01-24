package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "disasters")
public class SHDisasterMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, length = 22)
    private Integer sn;

    @Column(nullable = false)
    private LocalDateTime crt_dt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String msg_cn;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String rcptn_rgn_nm;

    @Column(nullable = false)
    private String emrg_step_nm;

    @Column(nullable = false)
    private String dst_se_nm;

    @Column(nullable = false)
    private LocalDateTime reg_ymd;

    @Column(nullable = false)
    private LocalDateTime mdfcn_ymd;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public LocalDateTime getCrt_dt() {
        return crt_dt;
    }

    public void setCrt_dt(LocalDateTime crt_dt) {
        this.crt_dt = crt_dt;
    }

    public String getMsg_cn() {
        return msg_cn;
    }

    public void setMsg_cn(String msg_cn) {
        this.msg_cn = msg_cn;
    }

    public String getRcptn_rgn_nm() {
        return rcptn_rgn_nm;
    }

    public void setRcptn_rgn_nm(String rcptn_rgn_nm) {
        this.rcptn_rgn_nm = rcptn_rgn_nm;
    }

    public String getEmrg_step_nm() {
        return emrg_step_nm;
    }

    public void setEmrg_step_nm(String emrg_step_nm) {
        this.emrg_step_nm = emrg_step_nm;
    }

    public String getDst_se_nm() {
        return dst_se_nm;
    }

    public void setDst_se_nm(String dst_se_nm) {
        this.dst_se_nm = dst_se_nm;
    }

    public LocalDateTime getReg_ymd() {
        return reg_ymd;
    }

    public void setReg_ymd(LocalDateTime reg_ymd) {
        this.reg_ymd = reg_ymd;
    }

    public LocalDateTime getMdfcn_ymd() {
        return mdfcn_ymd;
    }

    public void setMdfcn_ymd(LocalDateTime mdfcn_ymd) {
        this.mdfcn_ymd = mdfcn_ymd;
    }

    @Override
    public String toString() {
        return "SHDisasterMessageEntity [id=" + id + ", sn=" + sn + ", crt_dt=" + crt_dt + ", msg_cn=" + msg_cn
                + ", rcptn_rgn_nm=" + rcptn_rgn_nm + ", emrg_step_nm=" + emrg_step_nm + ", dst_se_nm=" + dst_se_nm
                + ", reg_ymd=" + reg_ymd + ", mdfcn_ymd=" + mdfcn_ymd + "]";
    }
}
