package com.mettles.rioc.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.BaseLongIdEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "RIOC_X12278_SUBMISSION")
@Entity(name = "rioc_X12278Submission")
@PublishEntityChangedEvents
public class X12278Submission extends BaseLongIdEntity {

    public Recepient getIntendedRecepient() {
        return intendedRecepient;
    }

    public void setIntendedRecepient(Recepient intendedRecepient) {
        this.intendedRecepient = intendedRecepient;
    }

    public String getEsmdTransactionId() {
        return esmdTransactionId;
    }

    public void setEsmdTransactionId(String esmdTransactionId) {
        this.esmdTransactionId = esmdTransactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PADocument> getPaDocument() {
        return paDocument;
    }

    public void setPaDocument(List<PADocument> paDocument) {
        this.paDocument = paDocument;
    }

    public List<PAError> getPaError() {
        return paError;
    }

    public void setPaError(List<PAError> paError) {
        this.paError = paError;
    }

    public List<PANotificationSlot> getPaNotificationSlot() {
        return paNotificationSlot;
    }

    public void setPaNotificationSlot(List<PANotificationSlot> paNotificationSlot) {
        this.paNotificationSlot = paNotificationSlot;
    }

    public List<PAStatusChange> getPaStatusChange() {
        return paStatusChange;
    }

    public void setPaStatusChange(List<PAStatusChange> paStatusChange) {
        this.paStatusChange = paStatusChange;
    }

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup"})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "intended_recepient", nullable = false)
    protected Recepient intendedRecepient;

    @Column(name = "esmd_transaction_id")
    protected String esmdTransactionId;

    public String getPaMessage() {
        return paMessage;
    }

    public void setPaMessage(String paMessage) {
        this.paMessage = paMessage;
    }

    public String getPaUTN() {
        return paUTN;
    }

    public void setPaUTN(String paUTN) {
        this.paUTN = paUTN;
    }

    @Column(name = "pa_message")
    protected String paMessage;

    @Column(name = "pa_utn")
    protected String paUTN;

    public String getSuppDocesmdTransactionId() {
        return suppDocesmdTransactionId;
    }

    public void setSuppDocesmdTransactionId(String suppDocesmdTransactionId) {
        this.suppDocesmdTransactionId = suppDocesmdTransactionId;
    }

    public String getDocUnqID() {
        return docUnqID;
    }

    public void setDocUnqID(String docUnqID) {
        this.docUnqID = docUnqID;
    }


    public String getX12UnqID() {
        return x12UnqID;
    }

    public void setX12UnqID(String x12UnqID) {
        this.x12UnqID = x12UnqID;
    }

    @Column(name = "x12_Unq_id")
    protected String x12UnqID;

    @Column(name = "suppDoc_esmd_transaction_id")
    protected String suppDocesmdTransactionId;

    @Column(name = "doc_Unq_id")
    protected String docUnqID;

    @Column(name = "status")
    protected String status;

    public String getSuppDocMessage() {
        return suppDocMessage;
    }

    public void setSuppDocMessage(String suppDocMessage) {
        this.suppDocMessage = suppDocMessage;
    }

    @Column(name = "supp_Doc_Message")
    protected String suppDocMessage;

    @Composition
    @OneToMany(mappedBy ="x12278submissionID")
    protected List<PADocument> paDocument;

    @Composition
    @OneToMany(mappedBy = "x12278submissionID")
    protected List<PAError> paError;

    @Composition
    @OneToMany(mappedBy = "x12278submissionID")
    protected List<PANotificationSlot> paNotificationSlot;

    @Composition
    @OneToMany(mappedBy = "x12278submissionID")
    protected List<PAStatusChange> paStatusChange;


    public String getAttachmentControlNumber() {
        return attachmentControlNumber;
    }

    public void setAttachmentControlNumber(String attachmentControlNumber) {
        this.attachmentControlNumber = attachmentControlNumber;
    }

    @Column(name = "attach_control_num")
    protected String attachmentControlNumber;


}