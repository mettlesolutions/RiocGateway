package com.mettles.rioc.entity;

import com.haulmont.cuba.core.entity.*;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

@Table(name = "RIOC_PA_NOTIFICATION_SLOT")
@Entity(name = "rioc_PANotificationSlot")
public class PANotificationSlot extends BaseUuidEntity implements Versioned, Updatable, Creatable {

    public X12278Submission getX12278submissionID() {
        return x12278submissionID;
    }

    public void setX12278submissionID(X12278Submission x12278submissionID) {
        this.x12278submissionID = x12278submissionID;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    /*
    public Integer getSplitInformation() {
        return splitInformation;
    }

    public void setSplitInformation(Integer splitInformation) {
        this.splitInformation = splitInformation;
    } */

    @Override
    public Date getUpdateTs() {
        return updateTs;
    }

    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public Date getCreateTs() {
        return createTs;
    }

    @Override
    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "x12278submissionID")
    protected X12278Submission x12278submissionID ;

    @Column(name = "name")
    protected String field;

    @Column(name = "value")
    protected String value;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /*remove splitInformation replace String type notes
        @Column(name = "split_Information")
        protected Integer splitInformation;*/
    @Column(name = "notes", length = 50)
    protected String notes;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;

    @Version
    @Column(name = "VERSION", nullable = false)
    protected Integer version;

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    @Column(name = "trans_type")
    protected String transType;

    /*
    public TransType getTransType() {
        return transType == null ? null : TransType.fromId(transType);
    }

    public void setTransType(TransType transType) {
        this.transType = transType == null ? null : transType.getId();
    }

    @Column(name = "trans_type")
    protected String transType = TransType.X12.getId();
    */

}