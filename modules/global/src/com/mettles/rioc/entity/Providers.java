package com.mettles.rioc.entity;

import com.haulmont.cuba.core.entity.BaseLongIdEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;

@Table(name = "RIOC_PROVIDERS")
@Entity(name = "rioc_providers")
public class Providers extends BaseLongIdEntity {
    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.CASCADE)
    @JoinColumn(name = "SUBMISSION_ID")
    protected Submission submissionID;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "PROVIDER_NPI")
    protected Long provider_npi;

    @Column(name = "addr_line1")
    protected String addrLine1;

    @Column(name = "addr_line2")
    protected String addrLine2;

    @Column(name = "city")
    protected String city;

    @Column(name = "state")
    protected String state;

    @Column(name = "zipcode")
    protected String zipcode;

    @Column(name = "registered_for_emdr")
    protected Boolean registered_for_emdr = false;

    @Column(name = "last_submitted_transaction")
    protected String last_submitted_transaction = ProvLastSubmittedTranxType.unregister.getId();

    public ProvLastSubmittedTranxType getLast_submitted_transaction() {
        return last_submitted_transaction == null ? null : ProvLastSubmittedTranxType.fromId(last_submitted_transaction);
    }

    public void setLast_submitted_transaction(ProvLastSubmittedTranxType last_submitted_transaction) {
        this.last_submitted_transaction = last_submitted_transaction == null ? null : last_submitted_transaction.getId();
    }

    public Boolean getRegistered_for_emdr() {
        return registered_for_emdr;
    }

    public void setRegistered_for_emdr(Boolean registered_for_emdr) {
        this.registered_for_emdr = registered_for_emdr;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setProvider_npi(Long provider_npi) {
        this.provider_npi = provider_npi;
    }

    public Long getProvider_npi() {
        return provider_npi;
    }

    public String getAddrLine2() {
        return addrLine2;
    }

    public void setAddrLine2(String addrLine2) {
        this.addrLine2 = addrLine2;
    }

    public String getAddrLine1() {
        return addrLine1;
    }

    public void setAddrLine1(String addrLine1) {
        this.addrLine1 = addrLine1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Submission getSubmissionID() {
        return submissionID;
    }

    public void setSubmissionID(Submission submissionID) {
        this.submissionID = submissionID;
    }
}