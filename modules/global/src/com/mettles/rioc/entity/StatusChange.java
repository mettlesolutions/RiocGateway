package com.mettles.rioc.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;

@Table(name = "RIOC_STATUS_CHANGE")
@Entity(name = "rioc_StatusChange")
public class StatusChange extends StandardEntity {
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