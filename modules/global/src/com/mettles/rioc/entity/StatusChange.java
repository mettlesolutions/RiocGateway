package com.mettles.rioc.entity;

import com.haulmont.cuba.core.entity.*;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

@Table(name = "RIOC_STATUS_CHANGE")
@Entity(name = "rioc_StatusChange")
public class StatusChange extends BaseUuidEntity implements Versioned, Updatable, Creatable {

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    protected Submission submissionId;

    @Column(name = "name", length = 512)
    protected String status;

    @Column(name = "result")
    protected String result;

    @Column(name = "split_information_moved0")
    protected String splitInformationMoved0;

    @Column(name = "split_information")
    protected String splitInformation;

    @Column(name = "esmd_transaction_id")
    protected String esmdTransactionId;

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

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
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
    public Date getCreateTs() {
        return createTs;
    }

    @Override
    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
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
    public Date getUpdateTs() {
        return updateTs;
    }

    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }


    public String getEsmdTransactionId() {
        return esmdTransactionId;
    }

    public void setEsmdTransactionId(String esmdTransactionId) {
        this.esmdTransactionId = esmdTransactionId;
    }

    public String getSplitInformation() {
        return splitInformation;
    }

    public void setSplitInformation(String splitInformation) {
        this.splitInformation = splitInformation;
    }

    public String getSplitInformationMoved0() {
        return splitInformationMoved0;
    }

    public void setSplitInformationMoved0(String splitInformationMoved0) {
        this.splitInformationMoved0 = splitInformationMoved0;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Submission getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Submission submissionId) {
        this.submissionId = submissionId;
    }
}