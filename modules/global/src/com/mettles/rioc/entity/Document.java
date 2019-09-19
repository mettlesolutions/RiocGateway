package com.mettles.rioc.entity;

import com.haulmont.cuba.core.entity.*;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.Date;

@Table(name = "RIOC_DOCUMENT")
@Entity(name = "rioc_Document")
public class Document extends BaseUuidEntity implements Versioned, Updatable, Creatable {
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
    @JoinColumn(name = "submissionID")
    protected Submission submissionID;

    @Column(name = "language")
    protected String language = SupportedLanguage.English.getId();

    @Column(name = "splitNumber")
    protected Integer splitNumber = 1;

    @Column(name = "attachmentcontrolnumber")
    protected String attachmentControlNumber;

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

    public DocumentType getDocument_type() {
        return document_type == null ? null : DocumentType.fromId(document_type);
    }

    public void setDocument_type(DocumentType document_type) {
        this.document_type = document_type == null ? null : document_type.getId();
    }

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

    public String getAttachmentControlNumber() {
        return attachmentControlNumber;
    }

    public void setAttachmentControlNumber(String attachmentControlNumber) {
        this.attachmentControlNumber = attachmentControlNumber;
    }

    public Integer getSplitNumber() {
        return splitNumber;
    }

    public void setSplitNumber(Integer splitNumber) {
        this.splitNumber = splitNumber;
    }

    public SupportedLanguage getLanguage() {
        return language == null ? null : SupportedLanguage.fromId(language);
    }

    public void setLanguage(SupportedLanguage language) {
        this.language = language == null ? null : language.getId();
    }

    public Submission getSubmissionID() {
        return submissionID;
    }

    public void setSubmissionID(Submission submissionID) {
        this.submissionID = submissionID;
    }

    public FileDescriptor getFileDescriptor() {
        return fileDescriptor;
    }

    public void setFileDescriptor(FileDescriptor fileDescriptor) {
        this.fileDescriptor = fileDescriptor;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}