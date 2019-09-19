package com.mettles.rioc.entity;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;

@Table(name = "RIOC_ADDRESS")
@Entity(name = "rioc_Address")
public class Address extends StandardEntity {

    @Column(name = "addrLine1", length = 512)
    protected String addressLine1;

    @Column(name = "addrLine2", length = 512)
    protected String code;

    @Column(name = "city", length = 512)
    protected String city;

    @Column(name = "zipcode", length = 512)
    protected String zipcode;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code")
    protected State state;

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }


}