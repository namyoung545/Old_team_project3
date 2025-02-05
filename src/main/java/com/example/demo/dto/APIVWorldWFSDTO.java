package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class APIVWorldWFSDTO {

    @NotBlank(message = "SERVICE는 필수 값입니다.")
    @JsonProperty("SERVICE")
    private String SERVICE;

    @NotBlank(message = "VERSION은 필수 값입니다.")
    @JsonProperty("VERSION")
    private String VERSION;

    @NotBlank(message = "REQUEST는 필수 값입니다.")
    @JsonProperty("REQUEST")
    private String REQUEST;

    @NotBlank(message = "OUTPUT은 필수 값입니다.")
    @JsonProperty("OUTPUT")
    private String OUTPUT;

    @NotBlank(message = "SRSNAME은 필수 값입니다.")
    @JsonProperty("SRSNAME")
    @Pattern(regexp = "EPSG:\\d+", message = "SRSNAME 형식이 잘못되었습니다. 예: EPSG:4326")
    private String SRSNAME;

    @NotBlank(message = "DOMAIN은 필수 값입니다.")
    @JsonProperty("DOMAIN")
    private String DOMAIN;

    @NotBlank(message = "TYPENAME은 필수 값입니다.")
    @JsonProperty("TYPENAME")
    private String TYPENAME;

    public String getSERVICE() {
        return SERVICE;
    }

    public void setSERVICE(String sERVICE) {
        SERVICE = sERVICE;
    }

    public String getVERSION() {
        return VERSION;
    }

    public void setVERSION(String vERSION) {
        VERSION = vERSION;
    }

    public String getREQUEST() {
        return REQUEST;
    }

    public void setREQUEST(String rEQUEST) {
        REQUEST = rEQUEST;
    }

    public String getOUTPUT() {
        return OUTPUT;
    }

    public void setOUTPUT(String oUTPUT) {
        OUTPUT = oUTPUT;
    }

    public String getSRSNAME() {
        return SRSNAME;
    }

    public void setSRSNAME(String sRSNAME) {
        SRSNAME = sRSNAME;
    }

    public String getDOMAIN() {
        return DOMAIN;
    }

    public void setDOMAIN(String dOMAIN) {
        DOMAIN = dOMAIN;
    }

    public String getTYPENAME() {
        return TYPENAME;
    }

    public void setTYPENAME(String tYPENAME) {
        TYPENAME = tYPENAME;
    }
}
