package com.mettles.rioc.entity;

import com.haulmont.cuba.core.entity.*;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

@Table(name = "RIOC_PA_DOCUMENT")
@Entity(name = "rioc_PADocument")
public class PADocument extends BaseUuidEntity implements Versioned, Updatable, Creatable {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public FileDescriptor getFileDescriptor() {
        return fileDescriptor;
    }

    public void setFileDescriptor(FileDescriptor fileDescriptor) {
        this.fileDescriptor = fileDescriptor;
    }

    public X12278Submission getX12278submissionID() {
        return x12278submissionID;
    }

    public void setX12278submissionID(X12278Submission x12278submissionID) {
        this.x12278submissionID = x12278submissionID;
    }

    public SupportedLanguage getLanguage() {
        return language == null ? null : SupportedLanguage.fromId(language);
    }

    public void setLanguage(SupportedLanguage language) {
        this.language = language == null ? null : language.getId();
    }

    /* public Integer getSplitNumber() {
        return splitNumber;
    }

    public void setSplitNumber(Integer splitNumber) {
        this.splitNumber = splitNumber;
    }
    */
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

    public DocumentType getDocument_type() {
        return document_type == null ? null : DocumentType.fromId(document_type);
    }

    public void setDocument_type(DocumentType document_type) {
        this.document_type = document_type == null ? null : document_type.getId();
    }

    @Column(name = "title")
    protected String title;

    @Column(name = "comments", length = 512)
    protected String comments;

    @Column(name = "filename")
    protected String filename;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fileDescriptorID")
    protected FileDescriptor fileDescriptor;


    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "x12278submissionID")
    protected X12278Submission x12278submissionID ;

    @Column(name = "language")
    protected String language = SupportedLanguage.English.getId();

    /*@Column(name = "splitNumber")
    protected Integer splitNumber = 1; */

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

    @Column(name = "document_type")
    protected String document_type = DocumentType.PDF.getId();

}