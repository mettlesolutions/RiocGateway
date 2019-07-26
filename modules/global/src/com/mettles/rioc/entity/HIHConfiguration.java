package com.mettles.rioc.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NamePattern("%s|hiHName")
@Table(name = "RIOC_HIH_CONFIGURATION")
@Entity(name = "rioc_HIHConfiguration")
public class HIHConfiguration extends StandardEntity {
    @NotNull
    @Column(name = "hiHName", nullable = false)
    protected String hiHName;

    @NotNull
    @Column(name = "hIIHOid", nullable = false)
    protected String hIIHOid;

    @NotNull
    @Column(name = "testRCOid", nullable = false)
    protected String testRCOid;

    @Column(name = "esMDIP", length = 512)
    protected String esMDIP;

    @NotNull
    @Column(name = "issuer", nullable = false)
    protected String issuer;

    @NotNull
    @Column(name = "testMode", nullable = false)
    protected Boolean testMode = false;

    @NotNull
    @Column(name = "payloadThreshold", nullable = false)
    protected Integer payloadThreshold;

    @NotNull
    @Column(name = "hIHDescription", nullable = false, length = 512)
    protected String hIHDescription;

    @NotNull
    @Column(name = "esMDUrl", nullable = false, length = 512)
    protected String esMDUrl;

    public String getEsMDUrl() {
        return esMDUrl;
    }

    public void setEsMDUrl(String esMDUrl) {
        this.esMDUrl = esMDUrl;
    }

    public String getHIHDescription() {
        return hIHDescription;
    }

    public void setHIHDescription(String hIHDescription) {
        this.hIHDescription = hIHDescription;
    }

    public Integer getPayloadThreshold() {
        return payloadThreshold;
    }

    public void setPayloadThreshold(Integer payloadThreshold) {
        this.payloadThreshold = payloadThreshold;
    }

    public Boolean getTestMode() {
        return testMode;
    }

    public void setTestMode(Boolean testMode) {
        this.testMode = testMode;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getEsMDIP() {
        return esMDIP;
    }

    public void setEsMDIP(String esMDIP) {
        this.esMDIP = esMDIP;
    }

    public String getTestRCOid() {
        return testRCOid;
    }

    public void setTestRCOid(String testRCOid) {
        this.testRCOid = testRCOid;
    }

    public String getHIIHOid() {
        return hIIHOid;
    }

    public void setHIIHOid(String hIIHOid) {
        this.hIIHOid = hIIHOid;
    }

    public String getHiHName() {
        return hiHName;
    }

    public void setHiHName(String hiHName) {
        this.hiHName = hiHName;
    }
}