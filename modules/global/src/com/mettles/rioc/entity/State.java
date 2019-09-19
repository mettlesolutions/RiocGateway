package com.mettles.rioc.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|statename")
@Table(name = "RIOC_STATE")
@Entity(name = "rioc_State")
public class State extends StandardEntity {

    @Column(name = "name", length = 512)
    protected String statename;

    @Column(name = "code", length = 512)
    protected String code;

    public String getStatename() {
        return statename;
    }

    public void setStatename(String statename) {
        this.statename = statename;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}