package com.mettles.rioc.entity;

import com.haulmont.cuba.core.entity.*;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

@Table(name = "RIOC_PA_ERROR")
@Entity(name = "rioc_PAError")
public class PAError extends BaseUuidEntity implements Versioned, Updatable, Creatable {

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "x12278submissionID")
    protected X12278Submission x12278submissionID ;

    public X12278Submission getX12278submissionID() {
        return x12278submissionID;
    }

    public void setX12278submissionID(X12278Submission x12278submissionID) {
        this.x12278submissionID = x12278submissionID;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCodeContext() {
        return codeContext;
    }

    public void setCodeContext(String codeContext) {
        this.codeContext = codeContext;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    /*
    public String getSplitInformationMoved0() {
        return splitInformationMoved0;
    }

    public void setSplitInformationMoved0(String splitInformationMoved0) {
        this.splitInformationMoved0 = splitInformationMoved0;
    }

    public Integer getSplitInformation() {
        return splitInformation;
    }

    public void setSplitInformation(Integer splitInformation) {
        this.splitInformation = splitInformation;
    }
    */

    public String getEsmdTransactionId() {
        return esmdTransactionId;
    }

    public void setEsmdTransactionId(String esmdTransactionId) {
        this.esmdTransactionId = esmdTransactionId;
    }

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

    @Column(name = "name")
    protected String error;

    @Column(name = "severity")
    protected String severity;

    @Column(name = "code_context")
    protected String codeContext;

    @Lob
    @Column(name = "description")
    protected String description;

    @Column(name = "error_code")
    protected String errorCode;

    /*
    @Column(name = "split_information_moved0")
    protected String splitInformationMoved0;

    @Column(name = "split_information")
    protected Integer splitInformation;
    */

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


    /*public TransType getTransType() {
        return transType == null ? null : TransType.fromId(transType);
    }

    public void setTransType(TransType transType) {
        this.transType = transType == null ? null : transType.getId();
    }

    @Column(name = "trans_type")
    protected String transType = TransType.X12.getId();
    */

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    @Column(name = "trans_type")
    protected String transType;

    @Version
    @Column(name = "VERSION", nullable = false)
    protected Integer version;

}