package com.mettles.rioc.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|intendedRecepient")
@Table(name = "RIOC_RECEPIENT")
@Entity(name = "rioc_Recepient")
public class Recepient extends StandardEntity {
    @Column(name = "name", length = 512)
    protected String intendedRecepient;

    @Column(name = "oid", length = 512)
    protected String oid;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getIntendedRecepient() {
        return intendedRecepient;
    }

    public void setIntendedRecepient(String intendedRecepient) {
        this.intendedRecepient = intendedRecepient;
    }
}