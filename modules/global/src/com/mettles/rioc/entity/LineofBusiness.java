package com.mettles.rioc.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s|purposeOfSubmission")
@Table(name = "RIOC_LINEOF_BUSINESS")
@Entity(name = "rioc_LineofBusiness")
public class LineofBusiness extends StandardEntity {
    @Column(name = "name", length = 512)
    protected String purposeOfSubmission;

    @Column(name = "content_type")
    protected String contentType;

    @Column(name = "is_esmd_claim_mandatory")
    protected Boolean isEsmdClaimIdMandatory = false;

    @Column(name = "is_esmd_claim_displayed")
    protected Boolean isEsmdClaimDisplayed = false;

    @Column(name = "is_esmd_caseid_displayed")
    protected Boolean isCaseIdDisplayed = false;

    public Boolean getIsCaseIdDisplayed() {
        return isCaseIdDisplayed;
    }

    public void setIsCaseIdDisplayed(Boolean isCaseIdDisplayed) {
        this.isCaseIdDisplayed = isCaseIdDisplayed;
    }

    public Boolean getIsEsmdClaimDisplayed() {
        return isEsmdClaimDisplayed;
    }

    public void setIsEsmdClaimDisplayed(Boolean isEsmdClaimDisplayed) {
        this.isEsmdClaimDisplayed = isEsmdClaimDisplayed;
    }

    public Boolean getIsEsmdClaimIdMandatory() {
        return isEsmdClaimIdMandatory;
    }

    public void setIsEsmdClaimIdMandatory(Boolean isEsmdClaimIdMandatory) {
        this.isEsmdClaimIdMandatory = isEsmdClaimIdMandatory;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPurposeOfSubmission() {
        return purposeOfSubmission;
    }

    public void setPurposeOfSubmission(String purposeOfSubmission) {
        this.purposeOfSubmission = purposeOfSubmission;
    }
}