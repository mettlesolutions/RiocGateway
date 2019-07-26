package com.mettles.rioc.entity;

import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Creatable;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.cuba.core.entity.Versioned;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.Date;

@Table(name = "RIOC_SPLIT_MAPS")
@Entity(name = "rioc_SplitMaps")
@PublishEntityChangedEvents
public class SplitMaps extends BaseUuidEntity implements Versioned, Updatable, Creatable {

    @Column(name = "name")
    protected String submissionUniqueId;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    protected Submission submissionId;

    @Column(name = "split_information")
    protected Integer splitInformation;

    @Column(name = "splitSubmissionStatus")
    protected String splitSubmissionStatus = "Draft";

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

    public String getSplitSubmissionStatus() {
        return splitSubmissionStatus;
    }

    public void setSplitSubmissionStatus(String splitSubmissionStatus) {
        this.splitSubmissionStatus = splitSubmissionStatus;
    }

    public Integer getSplitInformation() {
        return splitInformation;
    }

    public void setSplitInformation(Integer splitInformation) {
        this.splitInformation = splitInformation;
    }

    public Submission getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Submission submissionId) {
        this.submissionId = submissionId;
    }

    public String getSubmissionUniqueId() {
        return submissionUniqueId;
    }

    public void setSubmissionUniqueId(String submissionUniqueId) {
        this.submissionUniqueId = submissionUniqueId;
    }

}