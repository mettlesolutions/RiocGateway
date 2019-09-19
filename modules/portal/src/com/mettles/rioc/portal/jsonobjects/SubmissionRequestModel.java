package com.mettles.rioc.portal.jsonobjects;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public class SubmissionRequestModel {



    private String author_npi;
    private String intended_recepient;

    public boolean isAuto_split() {
        return auto_split;
    }

    public void setAuto_split(boolean auto_split) {
        this.auto_split = auto_split;
    }

    private boolean auto_split;
    private Integer threshold;
    private String name;
    private String esMD_claim_id;
    private String comments;
    private String fileContent;
    private String purpose_of_submission;

    public boolean isbSendinX12() {
        return bSendinX12;
    }

    public void setbSendinX12(boolean bSendinX12) {
        this.bSendinX12 = bSendinX12;
    }

    private boolean bSendinX12;




    public Set<DocumentSetJsonObject> getDocument_set() {
        return document_set;
    }

    public void setDocument_set(Set<DocumentSetJsonObject> document_set) {
        this.document_set = document_set;
    }

    private Set<DocumentSetJsonObject> document_set;


    public String getPurpose_of_submission() {
        return purpose_of_submission;
    }

    public void setPurpose_of_submission(String purpose_of_submission) {
        this.purpose_of_submission = purpose_of_submission;
    }



    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }



    public String getEsmd_case_id() {
        return esmd_case_id;
    }

    public void setEsmd_case_id(String esmd_case_id) {
        this.esmd_case_id = esmd_case_id;
    }

    private String esmd_case_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEsMD_claim_id() {
        return esMD_claim_id;
    }

    public void setEsMD_claim_id(String esMD_claim_id) {
        this.esMD_claim_id = esMD_claim_id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getIntended_recepient() {
        return intended_recepient;
    }

    public void setIntended_recepient(String intended_recepient) {
        this.intended_recepient = intended_recepient;
    }



    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }



    public String getAuthor_npi() {
        return author_npi;
    }

    public void setAuthor_npi(String author_npi) {
        this.author_npi = author_npi;
    }



}
