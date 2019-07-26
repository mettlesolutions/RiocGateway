package com.mettles.rioc.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.BaseLongIdEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Table(name = "RIOC_SUBMISSION")
@Entity(name = "rioc_Submission")
@PublishEntityChangedEvents
public class Submission extends BaseLongIdEntity {
    @Column(name = "patient_address_zip")
    protected String patientAddressZip;

    @Column(name = "last_name")
    protected String lastName;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup"})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "intended_recepient")
    protected Recepient intendedRecepient;

    @Lob
    @Column(name = "message")
    protected String message;

    @Column(name = "is_non_claimant")
    protected Boolean isNonClaimant;

    @Column(name = "first_name")
    protected String firstName;

    @Column(name = "has_first_witness_signature")
    protected Boolean hasFirstWitnessSignature;

    @Column(name = "middle_name")
    protected String middleName;

    @Lob
    @Column(name = "comments")
    protected String comments;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    protected Date dateOfBirth;

    @Column(name = "is_sample")
    protected Boolean isSample;

    @Column(name = "patient_address_city")
    protected String patientAddressCity;

    @Column(name = "author_type")
    protected String authorType;

    @Column(name = "phonenumber")
    protected String phonenumber;

    @Column(name = "has_second_witness_signature")
    protected Boolean hasSecondWitnessSignature;

    @Column(name = "patient_address")
    protected String patientAddress;

    @Column(name = "social_security_number")
    protected String socialSecurityNumber;

    @Column(name = "patient_address_street")
    protected String patientAddressStreet;

    @NotNull
    @Column(name = "author_npi", nullable = false)
    protected String authorNPI;

    @Column(name = "has_signature_of_patient")
    protected Boolean hasSignatureOfPatient;

    @Column(name = "stage")
    protected String stage = "Draft";

    @NotNull
    @Column(name = "name", nullable = false)
    protected String title;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup"})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purpose_of_submission")
    protected LineofBusiness purposeOfSubmission;

    @Column(name = "esmd_claim_id")
    protected String esMDClaimID;

    @Column(name = "status")
    protected String status;

    @Column(name = "patient_address_state")
    protected String patientAddressState;

    @Column(name = "has_message")
    protected Boolean hasMessage;

    @Column(name = "esmd_case_id")
    protected String esmdCaseId;

    @Column(name = "esmd_transaction_id")
    protected String esmdTransactionId;

    @Column(name = "highest_split_no")
    protected Integer highestSpiltNo;

    @Column(name = "last_submitted_split")
    protected Integer lastSubmittedSplit;

    @Lob
    @Column(name = "unique_id_list")
    protected String uniqueIdList;

    @Column(name = "transaction_id_list")
    protected String transactionIdList;

    @Column(name = "threshold")
    protected Integer threshold;

    @Column(name = "auto_split")
    protected Boolean autoSplit = false;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @Composition
    @OneToMany(mappedBy = "submissionId")
    protected List<Error> error;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @Composition
    @OneToMany(mappedBy = "submissionId")
    protected List<NotificationSlot> notificationSlot;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @Composition
    @OneToMany(mappedBy = "submissionId")
    protected List<StatusChange> statusChange;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hIHConfigID")
    protected HIHConfiguration hIHConfig;

    @Composition
    @OneToMany(mappedBy = "submissionID")
    protected List<Document> document;

    public void setDocument(List<Document> document) {
        this.document = document;
    }

    public List<Document> getDocument() {
        return document;
    }

    public HIHConfiguration getHIHConfig() {
        return hIHConfig;
    }

    public void setHIHConfig(HIHConfiguration hIHConfig) {
        this.hIHConfig = hIHConfig;
    }

    public List<StatusChange> getStatusChange() {
        return statusChange;
    }

    public void setStatusChange(List<StatusChange> statusChange) {
        this.statusChange = statusChange;
    }

    public List<NotificationSlot> getNotificationSlot() {
        return notificationSlot;
    }

    public void setNotificationSlot(List<NotificationSlot> notificationSlot) {
        this.notificationSlot = notificationSlot;
    }

    public List<Error> getError() {
        return error;
    }

    public void setError(List<Error> error) {
        this.error = error;
    }

    public void setAuthorType(AuthorType authorType) {
        this.authorType = authorType == null ? null : authorType.getId();
    }

    public AuthorType getAuthorType() {
        return authorType == null ? null : AuthorType.fromId(authorType);
    }

    public Boolean getAutoSplit() {
        return autoSplit;
    }

    public void setAutoSplit(Boolean autoSplit) {
        this.autoSplit = autoSplit;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public String getTransactionIdList() {
        return transactionIdList;
    }

    public void setTransactionIdList(String transactionIdList) {
        this.transactionIdList = transactionIdList;
    }

    public String getUniqueIdList() {
        return uniqueIdList;
    }

    public void setUniqueIdList(String uniqueIdList) {
        this.uniqueIdList = uniqueIdList;
    }

    public Integer getLastSubmittedSplit() {
        return lastSubmittedSplit;
    }

    public void setLastSubmittedSplit(Integer lastSubmittedSplit) {
        this.lastSubmittedSplit = lastSubmittedSplit;
    }

    public Integer getHighestSpiltNo() {
        return highestSpiltNo;
    }

    public void setHighestSpiltNo(Integer highestSpiltNo) {
        this.highestSpiltNo = highestSpiltNo;
    }

    public String getEsmdTransactionId() {
        return esmdTransactionId;
    }

    public void setEsmdTransactionId(String esmdTransactionId) {
        this.esmdTransactionId = esmdTransactionId;
    }

    public String getEsmdCaseId() {
        return esmdCaseId;
    }

    public void setEsmdCaseId(String esmdCaseId) {
        this.esmdCaseId = esmdCaseId;
    }

    public Boolean getHasMessage() {
        return hasMessage;
    }

    public void setHasMessage(Boolean hasMessage) {
        this.hasMessage = hasMessage;
    }

    public String getPatientAddressState() {
        return patientAddressState;
    }

    public void setPatientAddressState(String patientAddressState) {
        this.patientAddressState = patientAddressState;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEsMDClaimID() {
        return esMDClaimID;
    }

    public void setEsMDClaimID(String esMDClaimID) {
        this.esMDClaimID = esMDClaimID;
    }

    public LineofBusiness getPurposeOfSubmission() {
        return purposeOfSubmission;
    }

    public void setPurposeOfSubmission(LineofBusiness purposeOfSubmission) {
        this.purposeOfSubmission = purposeOfSubmission;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Boolean getHasSignatureOfPatient() {
        return hasSignatureOfPatient;
    }

    public void setHasSignatureOfPatient(Boolean hasSignatureOfPatient) {
        this.hasSignatureOfPatient = hasSignatureOfPatient;
    }

    public String getAuthorNPI() {
        return authorNPI;
    }

    public void setAuthorNPI(String authorNPI) {
        this.authorNPI = authorNPI;
    }

    public String getPatientAddressStreet() {
        return patientAddressStreet;
    }

    public void setPatientAddressStreet(String patientAddressStreet) {
        this.patientAddressStreet = patientAddressStreet;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public Boolean getHasSecondWitnessSignature() {
        return hasSecondWitnessSignature;
    }

    public void setHasSecondWitnessSignature(Boolean hasSecondWitnessSignature) {
        this.hasSecondWitnessSignature = hasSecondWitnessSignature;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPatientAddressCity() {
        return patientAddressCity;
    }

    public void setPatientAddressCity(String patientAddressCity) {
        this.patientAddressCity = patientAddressCity;
    }

    public Boolean getIsSample() {
        return isSample;
    }

    public void setIsSample(Boolean isSample) {
        this.isSample = isSample;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Boolean getHasFirstWitnessSignature() {
        return hasFirstWitnessSignature;
    }

    public void setHasFirstWitnessSignature(Boolean hasFirstWitnessSignature) {
        this.hasFirstWitnessSignature = hasFirstWitnessSignature;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Boolean getIsNonClaimant() {
        return isNonClaimant;
    }

    public void setIsNonClaimant(Boolean isNonClaimant) {
        this.isNonClaimant = isNonClaimant;
    }

    public String getMessage() {
        return message;
    }

    public Recepient getIntendedRecepient() {
        return intendedRecepient;
    }

    public void setIntendedRecepient(Recepient intendedRecepient) {
        this.intendedRecepient = intendedRecepient;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatientAddressZip() {
        return patientAddressZip;
    }

    public void setPatientAddressZip(String patientAddressZip) {
        this.patientAddressZip = patientAddressZip;
    }
}